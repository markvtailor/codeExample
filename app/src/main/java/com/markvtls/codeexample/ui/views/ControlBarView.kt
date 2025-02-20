package com.wallpaperscraft.keby.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.TextViewCompat
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.utils.createColorStateList
import com.wallpaperscraft.keby.app.utils.getDefaultControlBarTheme
import com.wallpaperscraft.keby.app.utils.safeParseColor
import com.wallpaperscraft.keby.databinding.ViewControlBarBinding
import com.wallpaperscraft.keby.domain.models.Background
import com.wallpaperscraft.keby.domain.models.Color
import com.wallpaperscraft.keby.domain.models.ControlBarState
import com.wallpaperscraft.keby.domain.models.ControlPanelTheme

class ControlBarView : FrameLayout {

    private val binding = ViewControlBarBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )
    private var state: ControlBarState = ControlBarState.ControlPanel
    private var theme: ControlPanelTheme = getDefaultControlBarTheme()
    private var onTextClickListener: ((text: String) -> Unit)? = null
    private var controlBarTextSize = DEFAULT_TEXT_SIZE

    init {
        binding.copiedText.setOnClickListener {
            onTextClickListener?.invoke(binding.copiedText.text.toString())
            setBarState(ControlBarState.ControlPanel)
        }

        applyCurrentTheme()
        handleControlPanelState()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun handleControlPanelState() = with(binding) {
        root.children.forEach { it.isVisible = false }

        when (val state = this@ControlBarView.state) {
            ControlBarState.ControlPanel -> {
                controlGroup.isVisible = true
            }

            is ControlBarState.Suggestions -> {
                backArrow.setImageResource(R.drawable.ic_back_arrow_right)
                backArrow.isVisible = true
                suggestions.isVisible = true
                setSuggestionsList(state.suggestions)
            }

            is ControlBarState.ClipboardText -> {
                backArrow.setImageResource(R.drawable.ic_back_arrow_right)
                backArrow.isVisible = true
                copiedText.isVisible = true
                copiedText.text = state.copiedText
            }

            ControlBarState.Clipboard -> {
                backArrow.setImageResource(R.drawable.ic_back)
                backArrow.isVisible = true
                addClipboard.isVisible = true
            }

            ControlBarState.Emoji -> {
                backArrow.setImageResource(R.drawable.ic_back)
                backArrow.isVisible = true
                deleteEmoji.isVisible = true
            }
        }
    }

    private fun setSuggestionsList(suggestions: List<String>) {
        binding.suggestions.removeAllViews()
        suggestions.forEachIndexed { index, suggestion ->
            if (index != 0) binding.suggestions.addView(getTextDivider())
            binding.suggestions.addView(getSuggestionView(suggestion))
        }
    }

    fun setBarState(state: ControlBarState) {
        if (this.state != state) {
            this.state = state
            handleControlPanelState()
        }
    }

    fun getBarState(): ControlBarState = state

    fun setOnTextClickListener(listener: ((text: String) -> Unit)) {
        onTextClickListener = listener
    }

    fun setTheme(theme: ControlPanelTheme) {
        this.theme = theme
        applyCurrentTheme()
    }

    fun setBackClickListener(listener: OnClickListener) {
        binding.backArrow.setOnClickListener(listener)
    }

    fun setAddClipboardTextListener(listener: OnClickListener) {
        binding.addClipboard.setOnClickListener(listener)
    }

    fun setVoiceInputClickListener(listener: OnClickListener) {
        binding.voiceInput.setOnClickListener(listener)
    }

    fun setSettingsClickListener(listener: OnClickListener) {
        binding.settings.setOnClickListener(listener)
    }

    fun setKebyIconClickListener(listener: OnClickListener) {
        binding.keby.setOnClickListener(listener)
    }

    fun setDeleteEmojiClickListener(listener: OnClickListener) {
        binding.deleteEmoji.setOnClickListener(listener)
    }

    fun setAssignmentClickListener(listener: OnClickListener) {
        binding.assignment.setOnClickListener(listener)
    }

    private fun applyCurrentTheme() = with(binding) {
        when (val background = theme.background) {
            is Background.ColorBackground -> {
                root.setBackgroundColor(safeParseColor(background.color.defaultHex))
            }

            is Background.DrawableBackground -> {
                root.background = ResourcesCompat.getDrawable(
                    resources, background.defaultRes,
                    null
                )
            }
        }

        //Set ImageViews state
        createColorStateList()
        val iconColors = when (val color = theme.iconColors) {
            is Color.ColorWithState -> {
                createColorStateList(color.defaultHex, color.tappedHex)
            }
            is Color.ColorWithoutState -> {
                createColorStateList(color.defaultHex)
            }
        }

        //Красит все ImageView в цвета темы
        binding.root.children.forEach {
            if (it is ImageView) {
                ImageViewCompat.setImageTintList(it, iconColors)
            }
        }

        copiedText.applyCurrentTheme()
        requestLayout()
    }

    private fun TextView.applyCurrentTheme() {
        val textColors = when (val color = theme.textColor) {
            is Color.ColorWithState -> {
                createColorStateList(color.defaultHex, color.tappedHex)
            }
            is Color.ColorWithoutState -> {
                createColorStateList(color.defaultHex)
            }
        }

        setTextColor(textColors)
        TextViewCompat.setCompoundDrawableTintList(this, textColors)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, controlBarTextSize)
    }

    private fun getTextDivider(): View {
        val horizontalMargins =
            resources.getDimension(R.dimen.text_divider_horizontal_margins).toInt()
        val verticalMargins =
            resources.getDimension(R.dimen.text_divider_vertical_margins).toInt()

        return View(context).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.text_divider))
            layoutParams = LayoutParams(
                resources.getDimension(R.dimen.text_divider_width).toInt(),
                LayoutParams.MATCH_PARENT,
            ).apply {
                setMargins(horizontalMargins, verticalMargins, horizontalMargins, verticalMargins)
            }
        }
    }

    private fun getSuggestionView(suggestion: String): View {
        return AppCompatTextView(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F)
            text = suggestion
            gravity = Gravity.CENTER
            maxLines = 1
            applyCurrentTheme()
            setOnClickListener {
                onTextClickListener?.invoke(text.toString())
            }
        }
    }

    companion object {
        private const val DEFAULT_TEXT_SIZE = 16f
    }
}