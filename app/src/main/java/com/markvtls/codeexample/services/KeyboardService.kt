package com.markvtls.codeexample.services

import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.inputmethodservice.InputMethodService
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.text.Spannable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection.GET_TEXT_WITH_STYLES
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.emoji2.text.EmojiCompat
import androidx.emoji2.text.EmojiSpan
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ConfigController
import com.wallpaperscraft.keby.app.KeyboardManager
import com.wallpaperscraft.keby.app.dictionary.DamerauLevenshtein
import com.wallpaperscraft.keby.app.dictionary.DictionaryCollection
import com.wallpaperscraft.keby.app.dictionary.DictionaryCollection.Companion.SUGGESTIONS_SIZE
import com.wallpaperscraft.keby.app.ui.BaseActivity
import com.wallpaperscraft.keby.app.ui.views.ClipboardView
import com.wallpaperscraft.keby.app.utils.createColorStateList
import com.wallpaperscraft.keby.app.utils.getLocale
import com.wallpaperscraft.keby.app.utils.isSingleOrAccentedChar
import com.wallpaperscraft.keby.databinding.DialogAddClipboardBinding
import com.wallpaperscraft.keby.databinding.LayoutKeyboardBinding
import com.wallpaperscraft.keby.domain.models.ClipboardItem
import com.wallpaperscraft.keby.domain.models.ConfigurationListener
import com.wallpaperscraft.keby.domain.models.ControlBarState
import com.wallpaperscraft.keby.domain.models.DictionaryWord
import com.wallpaperscraft.keby.domain.models.EnterType
import com.wallpaperscraft.keby.domain.models.InputActions
import com.wallpaperscraft.keby.domain.models.KeyTypes
import com.wallpaperscraft.keby.domain.models.KeyboardActionListener
import com.wallpaperscraft.keby.domain.models.KeyboardsType
import com.wallpaperscraft.keby.domain.models.PopupValue
import com.wallpaperscraft.keby.domain.models.SupportedLocales
import com.wallpaperscraft.keby.domain.usecases.ClipboardUseCase
import com.wallpaperscraft.keby.domain.usecases.DictionaryUseCase
import com.wallpaperscraft.keby.domain.usecases.WordBigramUseCase
import com.wallpaperscraft.keby.domain.usecases.settings.GetSettingsUseCase
import com.wallpaperscraft.keby.domain.usecases.settings.SharedDataUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class KeyboardService : InputMethodService(), ConfigurationListener {

    private val binding: LayoutKeyboardBinding
        get() = _binding!!
    private var _binding: LayoutKeyboardBinding? = null

    private val clipboardService: ClipboardManager by lazy {
        applicationContext.getSystemService(
            CLIPBOARD_SERVICE
        ) as ClipboardManager
    }
    private val onTextCopyListener: OnPrimaryClipChangedListener = OnPrimaryClipChangedListener {
        CoroutineScope(Dispatchers.IO).launch {
            val primaryClip = clipboardService.primaryClip
            if (primaryClip != null && primaryClip.itemCount != 0) {
                primaryClip.getItemAt(0).text?.let { clipboardUseCase.addItem(it.toString()) }
            }
        }
    }

    private val trieDictionaryCollection = DictionaryCollection()
    private var bigramCollection: MutableMap<String, MutableMap<String, Long>> = mutableMapOf()

    @Inject
    lateinit var clipboardUseCase: ClipboardUseCase

    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase

    @Inject
    lateinit var dictionaryUseCase: DictionaryUseCase

    @Inject
    lateinit var bigramUseCase: WordBigramUseCase

    @Inject
    lateinit var sharedDataUseCase: SharedDataUseCase

    @Inject
    lateinit var keyboardManager: KeyboardManager

    private val jobToCancelBuffer: ArrayList<Job> =
        arrayListOf() // Буффер для остановки корутин при onDestroy
    private val punctuationRegex by lazy { Regex("[?.!,]") }
    private val newLineRegex by lazy { Regex(".*\\n") }
    private val punctuationUppercaseRegex by lazy { Regex("[?.!] ") }

    private val dictionaryJobBuffer: MutableMap<SupportedLocales, Job> =
        mutableMapOf()

    private val bigramJobBuffer: MutableMap<SupportedLocales, Job> =
        mutableMapOf()

    private lateinit var editorInfo: EditorInfo
    private var addClipboardTextDialog: AlertDialog? = null
    private val keyboardActionListener = object : KeyboardActionListener {
        override fun onSelectKey(charCodes: IntArray) {
            KeyboardSoundManager.playSound()
            if (charCodes.isSingleOrAccentedChar()) {
                when (KeyTypes.getKeyType(charCodes.first())) {
                    KeyTypes.SHIFT -> {
                        keyboardManager.toggleShifted()
                        binding.keyboardView.invalidateAllKeys()
                    }

                    KeyTypes.ABC_SYMBOL_CHANGE -> {
                        switchKeyboard(
                            if (keyboardManager.getCurrentKeyboardType() == KeyboardsType.ABC) {
                                KeyboardsType.SYMBOL
                            } else {
                                KeyboardsType.ABC
                            }
                        )
                    }

                    KeyTypes.PHONE_ALT_CHANGE -> {
                        switchKeyboard(KeyboardsType.PHONE_ADDITIONAL)
                    }

                    KeyTypes.PHONE_NUMBER_CHANGE -> {
                        switchKeyboard(KeyboardsType.PHONE)
                    }

                    KeyTypes.WAIT -> {
                        //TODO ?!
                    }

                    KeyTypes.PAUSE -> {
                        //TODO ?!
                    }

                    KeyTypes.SYMBOL_ALT_CHANGE -> {
                        switchKeyboard(
                            if (keyboardManager.getCurrentKeyboardType() == KeyboardsType.SYMBOL) {
                                KeyboardsType.SYMBOL_ALTERNATIVE
                            } else {
                                KeyboardsType.SYMBOL
                            }
                        )
                    }

                    KeyTypes.ENTER -> {
                        when (keyboardManager.getEnterType()) {
                            EnterType.DONE -> {
                                handleInputAction(InputActions.ToNextLine)
                            }

                            else -> {
                                currentInputConnection.performEditorAction(
                                    editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
                                )
                            }
                        }
                    }

                    KeyTypes.DELETE -> {
                        if (currentInputConnection.getSelectedText(GET_TEXT_WITH_STYLES) != null) {
                            handleInputAction(InputActions.DeleteAll)
                        } else {
                            handleInputAction(InputActions.DeleteLast)
                        }

                        if (getSettingsUseCase.getSuggestionsRowSettingsValue()) revertSuggestionsOnBackspace()


                    }

                    else -> {
                        handleInputAction(InputActions.CommitCharCodes(charCodes))
                        getSuggestions(keyboardManager.getCurrentLanguage())
                        if (!keyboardManager.getCurrentKeyboard().isShiftLocked()) {
                            keyboardManager.toggleShifted(false)
                            binding.keyboardView.invalidateAllKeys()
                        }
                    }
                }
            } else {
                handleInputAction(InputActions.CommitCharCodes(charCodes))
            }


            when (KeyTypes.getKeyType(charCodes.first())) {
                KeyTypes.SHIFT -> {
                    //Чтобы не сбивать ShiftLocked
                }

                KeyTypes.DELETE -> {
                    if (checkIfTextShouldBeUppercase() && !keyboardManager.isShiftLocked()) {
                        keyboardManager.toggleShifted(true)
                        binding.keyboardView.invalidateAllKeys()
                    } else if (!keyboardManager.isShiftLocked()) {
                        keyboardManager.toggleShifted(false)
                        binding.keyboardView.invalidateAllKeys()
                    }
                }

                else -> {
                    if (checkIfTextShouldBeUppercase() && !keyboardManager.isShifted()) {
                        keyboardManager.toggleShifted()
                        binding.keyboardView.invalidateAllKeys()
                    }
                }
            }

        }

        override fun onSelectPopup(value: PopupValue) {
            when (value) {
                PopupValue.Emoji -> {
                    KeyboardSoundManager.playSound()
                    openEmoji()
                }

                is PopupValue.Text -> {
                    handleInputAction(
                        InputActions.CommitText(value.text)
                    )
                }
            }
        }

        override fun onStopInput() {
            requestHideSelf(0)
        }
    }

    override fun onCreate() {
        setTheme(R.style.Theme_Keby)
        super.onCreate()

        KeyboardSoundManager.init(applicationContext)
        if (!sharedDataUseCase.checkIfBuiltinDictionariesInited()) {
            saveDictionary(resources.openRawResource(R.raw.en_wordlist), SupportedLocales.EN)
            saveBigram(resources.openRawResource(R.raw.bigram_eng), SupportedLocales.EN)
            sharedDataUseCase.saveBuiltinDictionariesInited()

            dictionaryJobBuffer.forEach { job ->
                job.value.invokeOnCompletion {
                    initDictionaryCollection(job.key)
                }
            }
            bigramJobBuffer.forEach { job ->
                job.value.invokeOnCompletion {
                    initBigramCollection(job.key)
                }
            }
        } else {
            initDictionaryCollection(SupportedLocales.EN)
            initBigramCollection(SupportedLocales.EN)
        }
        ConfigController.addConfigurationListener(this)
        clipboardService.addPrimaryClipChangedListener(onTextCopyListener)
    }

    override fun onThemeChanged() {
        super.onThemeChanged()
        if (_binding != null && window.window != null) {
            window.window?.navigationBarColor = MaterialColors.getColor(
                binding.keyboardView, android.R.attr.navigationBarColor
            )
        }
    }

    override fun onDeviceOrientationChanged(orientation: Int, newWidthDp: Int) {
//        TODO KEBY-30 Доработать поворот без пересоздания клавиатур
//        keyboards.values.forEach {
//            it.rescaleKeyboard(orientation, newWidthDp)
//        }

        binding.keyboardView.hideAllPopUp()
    }

    private fun changeLanguage() {
        keyboardManager.toNextLanguage()
        binding.keyboardView.setKeyboard(keyboardManager.getCurrentKeyboard())
        getSuggestions(keyboardManager.getCurrentLanguage())
    }

    override fun onCreateInputView(): View {
        keyboardManager.init(this)
        KeyTypes
        //TODO: Переработать подгрузку
        //initDictionaryCollection(DictionaryLocales.EN)
        //initBigramCollection(DictionaryLocales.EN)
//        TODO KEBY-30 Доработать поворот без пересоздания клавиатур
//        keyboards.forEach { it.value.updateDefaultOrientation(orientation) }
        _binding = LayoutKeyboardBinding.inflate(layoutInflater)
        // TODO Set custom theme
        // binding.keyboardView.setKeyboardTheme(THEME)

        binding.keyboardView.setOnChangeLanguageListener {
            changeLanguage()
        }
        addClipboardTextDialog = createAddClipboardTextDialog()
        initControlBar()
        initClipboard()
        initEmoji()

        return binding.root
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)

        editorInfo = info
        keyboardManager.updateKeyboard(
            this,
            KeyboardManager.Companion.KeyboardAdditionalParameters(
                numericEnabled = getSettingsUseCase.isNumericRowKeyboardEnabled(),
                alternativeSymbolsEnabled = getSettingsUseCase.isAlternativeSymbolicKeyboardEnabled()
            )
        )
        KeyboardSoundManager.apply {
            setEnabled(getSettingsUseCase.getClickSoundEnabled())
            setVolume(getSettingsUseCase.getClickSoundVolume())
        }
        configureKeyboardView()
        if (getCurrentWord().isNotEmpty() && binding.controlBar.getBarState() != ControlBarState.Clipboard) {
            getSuggestions(keyboardManager.getCurrentLanguage())
        }
    }

    private fun configureKeyboardView() {
        binding.keyboardView.apply {
            setActionListener(keyboardActionListener)
            val uriKeyboardType =
                editorInfo.inputType and EditorInfo.TYPE_MASK_VARIATION == EditorInfo.TYPE_TEXT_VARIATION_URI
            if (keyboardManager.setKeyboardAlternativeValues(uriKeyboardType)) {
                keyboardManager.updateEnterType(EnterType.NEXT)
            } else {
                keyboardManager.updateEnterType(
                    when (editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) {
                        EditorInfo.IME_ACTION_DONE -> EnterType.DONE
                        EditorInfo.IME_ACTION_NEXT -> EnterType.NEXT
                        EditorInfo.IME_ACTION_GO -> EnterType.GO
                        EditorInfo.IME_ACTION_SEARCH -> EnterType.SEARCH
                        else -> EnterType.NEXT
                    }
                )
            }
        }

        when (editorInfo.inputType and InputType.TYPE_MASK_CLASS) {
            EditorInfo.TYPE_CLASS_PHONE -> switchKeyboard(KeyboardsType.PHONE)

            EditorInfo.TYPE_CLASS_TEXT -> switchKeyboard(KeyboardsType.ABC)

            EditorInfo.TYPE_CLASS_NUMBER -> switchKeyboard(KeyboardsType.PHONE)

            EditorInfo.TYPE_CLASS_DATETIME -> switchKeyboard(KeyboardsType.PHONE)
        }
    }

    private fun switchKeyboard(type: KeyboardsType) {
        keyboardManager.setKeyboardType(type)
        binding.keyboardView.setKeyboard(keyboardManager.getCurrentKeyboard())
    }

    override fun onEvaluateFullscreenMode(): Boolean = false

    override fun onDestroy() {
        KeyboardSoundManager.release()
        clipboardService.removePrimaryClipChangedListener(onTextCopyListener)
        jobToCancelBuffer.apply {
            forEach { it.cancel() }
            clear()
        }
        ConfigController.removeConfigurationListener(this)

        try {
            binding.keyboardView.removeActionListener()
        } catch (exception: NullPointerException) {
            Log.e(KeyboardService::class.simpleName, "NPE", exception)
        }

        _binding = null
        super.onDestroy()
    }

    private fun getCurrentWord(): String {
        val currentText = try {
            currentInputConnection.getTextBeforeCursor(CHARS_BEFORE_CURSOR_DEFAULT, 0)
        } catch (exception: NullPointerException) {
            ""
        }


        return if (currentText.isNullOrEmpty()) "" else currentText.split(" ")
            .findLast { it.isNotBlank() } ?: ""
    }

    private fun getNextWordSuggestions() {
        val lastWord = getCurrentWord()
        if (lastWord.isNotEmpty()) {
            val suggestions = bigramCollection[lastWord.lowercase()]

            if (!suggestions.isNullOrEmpty()) {
                val sortedSuggestions = suggestions.toList().sortedBy { it.second }.reversed()
                val result =
                    if (sortedSuggestions.size <= SUGGESTIONS_SIZE) sortedSuggestions else sortedSuggestions.dropLast(
                        sortedSuggestions.size - SUGGESTIONS_SIZE
                    )
                binding.controlBar.setBarState(ControlBarState.Suggestions(result.map { it.first }))
            } else openControlPanel()
        } else openControlPanel()
    }

    private fun getSuggestions(dictionaryLocale: SupportedLocales) {
        if (getSettingsUseCase.getSuggestionsRowSettingsValue()) {
            if (keyboardManager.getCurrentKeyboardSet().isDictionaryInited) {
                val currentWord = getCurrentWord()
                if (currentWord.isNotEmpty() && checkIfWordValid(currentWord)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        var suggestions =
                            trieDictionaryCollection.getSuggestions(currentWord, dictionaryLocale)
                        if (suggestions.isEmpty()) {
                            suggestions = getSuggestionsCorrected(dictionaryLocale)
                        }
                        withContext(Dispatchers.Main) {
                            binding.controlBar.setBarState(
                                if (suggestions.isNotEmpty()) {
                                    ControlBarState.Suggestions(suggestions)
                                } else {
                                    ControlBarState.ControlPanel
                                }
                            )
                        }
                    }
                } else getNextWordSuggestions()
            } else openControlPanel()
        }
    }

    private fun getSuggestionsCorrected(dictionaryLocale: SupportedLocales): List<String> {
        val currentWord = getCurrentWord()
        val dictionary = trieDictionaryCollection.getRawDictionary(dictionaryLocale)
        val result = mutableListOf<DictionaryWord>()

        if (dictionary != null) {
            result.addAll(dictionary.asSequence().mapNotNull { dictWord ->
                val distance = DamerauLevenshtein.calculateDistance(currentWord, dictWord.word)
                if (distance == 1) dictWord else null
            }.take(3).sortedBy { it.frequency })
        }
        return result.map { it.word }
    }


    //TODO: Где и когда это должно вызываться? Загрузка требует времени, так что в идеале мы должны заполнять словарь ДО вызова клавиатуры
    private fun initDictionaryCollection(dictionaryLocale: SupportedLocales) {
        CoroutineScope(Dispatchers.IO).launch {
            trieDictionaryCollection.createDictionary(dictionaryLocale)
            val dictionary = dictionaryUseCase.getDictionary(dictionaryLocale)
            trieDictionaryCollection.setRawDictionary(dictionaryLocale, dictionary)

            for (word in dictionary) {
                trieDictionaryCollection.addWordToDictionary(
                    word.word,
                    word.frequency,
                    dictionaryLocale
                )
            }
        }
    }

    private fun initBigramCollection(bigramLocale: SupportedLocales) {
        CoroutineScope(Dispatchers.IO).launch {
            bigramCollection = bigramUseCase.getBigram(bigramLocale)
        }
    }

    private fun saveDictionary(dictionary: InputStream, locale: SupportedLocales) {
        CoroutineScope(Dispatchers.IO).launch {
            dictionaryUseCase.saveDictionary(dictionary, locale)
        }.also {
            dictionaryJobBuffer[locale] = it
        }
    }

    private fun saveBigram(bigram: InputStream, locale: SupportedLocales) {
        CoroutineScope(Dispatchers.IO).launch {
            bigramUseCase.saveBigram(bigram, locale)
        }.also {
            bigramJobBuffer[locale] = it
        }
    }

    private fun revertSuggestionsOnBackspace() {
        val currentText = getCurrentWord()

        if (currentText.isNotEmpty()) {
            val isCursorAtSymbol = currentText.last().isLetterOrDigit()

            if (isCursorAtSymbol) {
                getSuggestions(keyboardManager.getCurrentLanguage())
            }
        } else openControlPanel()
    }

    private fun handleSuggestionsClick(text: String) {
        val currentInputText = currentInputConnection.getTextBeforeCursor(
            CHARS_BEFORE_CURSOR_DEFAULT, 0
        )
        val last = currentInputText?.split(" ")?.last()
        val from = last?.length?.let { currentInputText.length.minus(it) }
        val to = currentInputText?.length

        if (to != null && from != null) {
            currentInputConnection.apply {
                beginBatchEdit()
                deleteSurroundingText(last.length, 0)
                handleInputAction(InputActions.CommitText("$text "))
                endBatchEdit()
            }
        }

        getNextWordSuggestions()
    }

    private fun initControlBar() {
        val settingsExtra = Bundle().apply {
            putBoolean(BaseActivity.INTENT_EXTRA_MOVE_TO_SETTINGS, true)
        }
        val startActivityIntent = Intent(this, BaseActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        binding.controlBar.apply {
            setOnTextClickListener { text ->
                handleSuggestionsClick(text)
            }
            setAssignmentClickListener {
                openClipboard()
            }
            setBackClickListener {
                returnToKeyboard()
            }
            setSettingsClickListener {
                startActivity(
                    startActivityIntent.apply {
                        putExtras(settingsExtra)
                    }
                )
            }
            setKebyIconClickListener {
                startActivity(startActivityIntent)
            }
        }
    }

    private fun openClipboard() {
        binding.controlBar.setBarState(ControlBarState.Clipboard)
        binding.keyboardView.visibility = View.INVISIBLE
        binding.clipboard.isVisible = true
        binding.emoji.isVisible = false
    }

    private fun returnToKeyboard() {
        binding.controlBar.setBarState(ControlBarState.ControlPanel)
        binding.keyboardView.isVisible = true
        binding.clipboard.isVisible = false
        binding.emoji.isVisible = false
    }

    private fun openEmoji() {
        binding.controlBar.setBarState(ControlBarState.Emoji)
        binding.keyboardView.visibility = View.INVISIBLE
        binding.clipboard.isVisible = false
        binding.emoji.isVisible = true
    }

    private fun openControlPanel() {
        binding.controlBar.setBarState(ControlBarState.ControlPanel)
    }

    private fun createAddClipboardTextDialog(): AlertDialog {
        val dialogBinding = DialogAddClipboardBinding.inflate(LayoutInflater.from(this))
        dialogBinding.saveButton.backgroundTintList = createColorStateList(
            defaultColor = ContextCompat.getColor(
                applicationContext, R.color.dialog_add_clipboard_item_button_save_background
            ), tappedColor = ContextCompat.getColor(
                applicationContext, R.color.dialog_add_clipboard_item_button_save_background_pressed
            )
        )

        createColorStateList(
            defaultColor = ContextCompat.getColor(
                applicationContext, R.color.dialog_add_clipboard_item_button_cancel_color
            ), tappedColor = ContextCompat.getColor(
                applicationContext, R.color.dialog_add_clipboard_item_button_cancel_color_pressed
            )
        ).let {
            dialogBinding.cancelButton.strokeColor = it
            dialogBinding.cancelButton.setTextColor(it)
        }

        val alertDialog = MaterialAlertDialogBuilder(this@KeyboardService).apply {
            setView(dialogBinding.root)
            setOnDismissListener {
                dialogBinding.clipboardText.text = null
                openClipboard()
            }
        }.create().apply {
            window?.apply {
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setType(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    } else {
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                    }
                )
            }
            setOnShowListener {
                dialogBinding.clipboardText.requestFocus()
            }
        }

        dialogBinding.apply {
            saveButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    clipboardUseCase.addItem(clipboardText.text.toString(), true)
                }

                clipboardText.text = null
                openClipboard()
                alertDialog.dismiss()
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        return alertDialog
    }

    private fun checkIfTextShouldBeUppercase(): Boolean {
        val currentText = currentInputConnection.getTextBeforeCursor(CHARS_BEFORE_UPPERCASE, 0)
        return when {
            currentText.isNullOrEmpty() -> true
            "${currentText.last()} ".matches(punctuationUppercaseRegex)
                    && getSettingsUseCase.getAutospaceSettingsValue()
                    && getSettingsUseCase.getAutocapsSettingsValue() -> true

            currentText.matches(newLineRegex) -> true
            currentText.matches(punctuationUppercaseRegex) && getSettingsUseCase.getAutocapsSettingsValue() -> true
            else -> false
        }
    }

    private fun handleInputAction(action: InputActions) {
        when (action) {
            is InputActions.CommitText -> {
                currentInputConnection.commitText(action.text, 1)
            }

            is InputActions.CommitCharCodes -> {
                var commitText = action.codes.map { Char(it) }.joinToString("")
                if (commitText.isBlank()) openControlPanel()
                if (binding.keyboardView.isShifted()) {
                    commitText = commitText.uppercase(getLocale())
                }
                if (checkIfAutospaceNeeded(commitText)) {
                    commitText = " $commitText"
                }
                currentInputConnection.commitText(commitText, 1)
            }

            InputActions.ToNextLine -> {
                currentInputConnection.commitText("\n", 1)
            }

            InputActions.DeleteAll -> {
                currentInputConnection.commitText("", 1)
            }

            InputActions.DeleteLast -> {
                currentInputConnection.deleteSurroundingText(getCharsCountForDelete(), 0)
            }
        }
    }

    private fun getCharsCountForDelete(): Int {
        val word = getCurrentWord()
        var count = 1

        if (word.isNotEmpty()) {
            val processed = EmojiCompat.get()
                .process(
                    word,
                    0,
                    word.length - 1,
                    Integer.MAX_VALUE,
                    EmojiCompat.REPLACE_STRATEGY_ALL
                )
            if (processed is Spannable) {
                val span = processed.getSpans(processed.length, processed.length, Any::class.java)
                    .lastOrNull()

                count = if (span != null && span is EmojiSpan) {
                    processed.getSpanEnd(span) - processed.getSpanStart(span)
                } else 1
            }
        }

        return count
    }

    private fun checkIfAutospaceNeeded(text: String): Boolean {
        return if (getSettingsUseCase.getAutospaceSettingsValue()) {
            val isInputTextPunctChar = text.matches(punctuationRegex)
            val isLastCharPunct =
                currentInputConnection.getTextBeforeCursor(CHARS_BEFORE_NEW_SENTENCE, 0)!!.trimEnd()
                    .matches(punctuationRegex)
            !isInputTextPunctChar && isLastCharPunct
        } else false
    }

    private fun checkCanDrawOverlayPermission(onPermissionGrantedListener: () -> Unit) {
        if (!Settings.canDrawOverlays(this@KeyboardService)) {
            val intent = Intent(INTENT_PERMISSION_OVERLAY).apply {
                setData(Uri.parse("package:$packageName"))
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            startActivity(intent)
        } else onPermissionGrantedListener.invoke()
    }

    private fun getDictionaryLocaleFromSystem(): SupportedLocales {
        val systemLocale = Locale.getDefault().language
        return SupportedLocales.values().find { it.mask == systemLocale } ?: SupportedLocales.NONE
    }

    private fun initClipboard() {
        CoroutineScope(Dispatchers.IO).launch {
            clipboardUseCase.getItems().collectLatest { items ->
                withContext(Dispatchers.Main) {
                    try {
                        binding.clipboard.setClipboardItems(items)
                    } catch (exception: NullPointerException) {
                        Log.e(KeyboardService::class.simpleName, "NPE", exception)
                    }
                }
            }
        }.also {
            jobToCancelBuffer.add(it)
        }

        binding.controlBar.setAddClipboardTextListener {
            checkCanDrawOverlayPermission(
                onPermissionGrantedListener = {
                    addClipboardTextDialog?.let {
                        returnToKeyboard()
                        switchKeyboard(KeyboardsType.ABC)
                        it.show()
                    }
                }
            )
        }

        binding.clipboard.setClipboardItemListener(object : ClipboardView.ClipboardItemListener {
            override fun onPinClick(item: ClipboardItem) {
                CoroutineScope(Dispatchers.IO).launch {
                    clipboardUseCase.pinItem(item)
                }
            }

            override fun onTextClick(item: ClipboardItem) {
                handleInputAction(InputActions.CommitText(item.text))
            }

            override fun onDeleteClick(item: ClipboardItem) {
                CoroutineScope(Dispatchers.IO).launch {
                    clipboardUseCase.deleteItem(item)
                }
            }
        })
    }

    private fun initEmoji() {
        binding.controlBar.setDeleteEmojiClickListener {
            handleInputAction(InputActions.DeleteLast)
        }
        binding.emoji.setOnPrintEmojiListener { emoji ->
            handleInputAction(InputActions.CommitText(emoji))
        }
    }

    private fun checkIfWordValid(word: String): Boolean {
        val chars = word.toCharArray()

        chars.forEach {
            if (!it.isDigit() || it !in "!.?".toCharArray()) {
                val isValid =
                    it.toString().matches(keyboardManager.getCurrentLanguage().regexPattern)
                if (!isValid) return false
            }
        }
        return true
    }

    companion object {
        private const val CHARS_BEFORE_NEW_SENTENCE = 1
        private const val CHARS_BEFORE_UPPERCASE = 2

        private const val CHARS_BEFORE_CURSOR_DEFAULT = 15

        private const val INTENT_PERMISSION_OVERLAY =
            "android.settings.action.MANAGE_OVERLAY_PERMISSION"
    }
}