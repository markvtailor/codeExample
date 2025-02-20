package com.wallpaperscraft.keby.app.ui.models

import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.util.Xml
import androidx.core.content.ContextCompat
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.models.Key.Companion.TAG_KEY
import com.wallpaperscraft.keby.app.ui.models.Row.Companion.TAG_ROW
import com.wallpaperscraft.keby.app.utils.getDimensionOrFraction
import com.wallpaperscraft.keby.app.utils.isOrientationLandscape
import com.wallpaperscraft.keby.domain.models.BaseKeyboard
import com.wallpaperscraft.keby.domain.models.EnterType
import com.wallpaperscraft.keby.domain.models.IconAlignment
import com.wallpaperscraft.keby.domain.models.IconState
import com.wallpaperscraft.keby.domain.models.KeyTypes
import com.wallpaperscraft.keby.domain.models.Shiftable
import org.xmlpull.v1.XmlPullParser

class Keyboard(private val context: Context, private val layoutRes: Int) : BaseKeyboard(),
    Shiftable {
    private var keyWidth: Int = 0
    private var keyHeight: Int = 0
    private var keyHorizontalGap = 0
    private var keyVerticalGap = 0
    private var keyboardWidth: Int = 0
    private var keyboardHeight: Int = 0
    private val specialKeys = HashMap<KeyTypes, Key>()
    private val rows: ArrayList<Row> = ArrayList()
    private val keys: ArrayList<Key> = ArrayList()
    private val displayWidth: Int = context.resources.displayMetrics.widthPixels
    private val displayHeight: Int = context.resources.displayMetrics.heightPixels
    private val keyboardMode: Int = 0
    private var defaultOrientation: Int = -1
    private var isKeysAlternativeValues = false
    override val millisToLockShift = Shiftable.DEFAULT_MILLIS_TO_LOCK_SHIFT
    override var shiftLocked = false
    override var lastTimeShiftTapMillis = 0L
    override var keysShifted = false

    init {
        loadKeyboard()
        scaleKeyboard(context.resources.configuration.screenWidthDp)
    }

    fun getKeyboardWidth() = keyboardWidth

    fun getKeyboardHeight() = keyboardHeight

    fun getKeys(): List<Key> = keys

    fun getIndexOfSpecialKey(type: KeyTypes): Int {
        val key = specialKeys[type]

        return if (key != null) {
            keys.indexOf(key)
        } else NOT_A_KEY
    }

    fun getDisplayWidth() = displayWidth

    fun getDisplayHeight() = displayHeight

    fun getKeyWidth() = keyWidth

    fun getKeyHeight() = keyHeight

    fun getKeyHorizontalGap() = keyHorizontalGap

    fun getKeyVerticalGap() = keyVerticalGap

    fun setKeysAlternativeValuesEnabled(enabled: Boolean) {
        isKeysAlternativeValues = enabled
        keys.filter { it.isAlternativeValueEnabled() }.forEach {
            if (enabled != it.isAlternativeValue()) {
                it.setAlternativeValues(enabled)
            }
        }
    }

    fun isKeysAlternativeValue() = isKeysAlternativeValues

    fun getKeyIndex(x: Int, y: Int): Int {
        for (index in keys.indices) {
            if (keys[index].isInside(x, y)) return index
        }
        return NOT_A_KEY
    }

    private fun loadKeyboard() {
        val parser = context.resources.getXml(layoutRes)
        var x = 0
        var y = 0
        var inKey = false
        var inRow = false
        var currentRow: Row? = null
        var currentKey: Key? = null
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    TAG_KEYBOARD -> {
                        parseKeyboardAttributes(context.resources, parser)
                    }

                    TAG_ROW -> {
                        x = 0
                        inRow = true
                        currentRow = Row(this, context.resources, parser)
                        rows.add(currentRow)
                        val skipRow = currentRow.getKeyboardMode() != 0 &&
                                currentRow.getKeyboardMode() != keyboardMode
                        if (skipRow) {
                            skipToEndOfRow(parser)
                            inRow = false
                        }
                    }

                    TAG_KEY -> {
                        inKey = true
                        if (currentRow != null) {
                            currentKey = Key(
                                x,
                                y,
                                currentRow,
                                context,
                                parser
                            )
                            keys.add(currentKey)

                            currentKey.getCodes()?.firstOrNull()?.let { code ->
                                KeyTypes.getKeyType(code)?.let { type ->
                                    specialKeys[type] = currentKey
                                }
                            }
                            currentRow.addKey(currentKey)
                        }
                    }
                }
            } else if (parser.eventType == XmlPullParser.END_TAG) {
                if (inKey) {
                    inKey = false
                    if (currentKey != null) {
                        x += currentKey.getWidth() + currentKey.getHorizontalGap()
                        if (x > keyboardWidth) {
                            keyboardWidth = x
                        }
                    }
                } else if (inRow) {
                    inRow = false
                    if (currentRow != null) {
                        y += currentRow.getKeyHeight() + currentRow.getKeyVerticalGap()
                    }
                }
            }
        }
        keyboardHeight = y - keyVerticalGap
    }

    private fun parseKeyboardAttributes(res: Resources, parser: XmlResourceParser) {
        parser.require(XmlPullParser.START_TAG, null, TAG_KEYBOARD)
        val attrs = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.Keyboard)
        keyWidth = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyWidth,
            displayWidth,
            keyWidth
        )
        keyHeight = attrs.getDimensionOrFraction(
            if (context.isOrientationLandscape()) {
                R.styleable.Keyboard_keyHeightLandscape
            } else R.styleable.Keyboard_keyHeightPortrait,
            displayHeight,
            keyHeight
        )
        keyHorizontalGap = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyHorizontalGap,
            displayWidth,
            keyHorizontalGap
        )
        keyVerticalGap = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyVerticalGap,
            displayHeight,
            keyVerticalGap
        )
        attrs.recycle()
    }

    override fun toggleShifted(shifted: Boolean?) {
        super.toggleShifted(shifted)

        specialKeys[KeyTypes.SHIFT]?.setIconsState(
            if (shiftLocked) {
                IconState.LOCKED
            } else if (keysShifted) {
                IconState.PRESSED
            } else {
                IconState.NORMAL
            }
        )
    }

    /**
     * Начальный просчет параметров клавиш
     *
     * TODO: Рефакторинг, возможная переработка
     * Первая строка должна быть без спецклавиш и border-to-border
     */
    private fun scaleKeyboard(displayWidthDp: Int) {
        val displayWidth = (displayWidthDp * context.resources.displayMetrics.density)
        val gap = getHorizontalKeyGap(displayWidthDp)
        val initialGap = getHorizontalKeyGap(displayWidthDp, true)
        var calculatedCommonKeyWidth = -1
        var xCoord: Int
        rows.forEach { row ->
            row.setHorizontalGap(gap)
            if (row.isBorderToBorder()) {
                // Размещение клавиш от края до края
                xCoord = initialGap
                val emptyDisplayWidth: Double =
                    displayWidth.toDouble() - ((row.getKeys().size - 1) * gap) - initialGap * 2
                val currentKeysWidth = row.getKeys().sumOf { it.getWidth() }
                val scaleModifier: Double = emptyDisplayWidth / currentKeysWidth
                row.getKeys().forEach { key ->
                    val newKeyWidth = if (key.getWidth() == row.getKeyWidth()) { // Common key
                        if (calculatedCommonKeyWidth != -1) {
                            calculatedCommonKeyWidth
                        } else {
                            (key.getWidth() * scaleModifier).toInt().also {
                                calculatedCommonKeyWidth = it
                            }
                        }
                    } else {
                        (key.getWidth() * scaleModifier).toInt()
                    }

                    key.setX(xCoord)
                    key.setWidth(newKeyWidth)
                    xCoord += newKeyWidth + gap
                    key.initDefaultParams()
                }
            } else {
                // Размещение клавиш по центру
                if (row.getKeys().find { it.getWidth() != row.getKeyWidth() }
                    == null && calculatedCommonKeyWidth != -1) { // Если в строке нет спецклавиш и размер обычной клавиши рассчитан
                    xCoord =
                        ((displayWidth - (row.getKeys().size - 1) * gap - row.getKeys().size * calculatedCommonKeyWidth) / 2.0).toInt()

                    row.getKeys().forEach { key ->
                        key.setX(xCoord)
                        key.setWidth(calculatedCommonKeyWidth)
                        xCoord += calculatedCommonKeyWidth + gap
                        key.initDefaultParams()
                    }
                }
            }
        }
    }

    /**
     * Пересчет координат и размеров клавиш при повороте экрана
     * TODO KEBY-30 Доработкать поворот без пересоздания клавиатур
     * TODO: Рефакторинг, возможная переработка
     * Первая строка должна быть без спецклавиш и border-to-border
     */
    fun rescaleKeyboard(orientation: Int, displayWidthDp: Int) {
        val displayWidth = (displayWidthDp * context.resources.displayMetrics.density)
        var calculatedCommonKeyWidth = -1

        rows.forEach { row ->
            val gap = getHorizontalKeyGap(displayWidthDp)
            val initialGap = getHorizontalKeyGap(displayWidthDp, true)
            row.setHorizontalGap(gap)
            val emptyDisplayWidth =
                displayWidth.toDouble() - ((row.getKeys().size - 1) * gap + 2 * initialGap)
            val currentKeysWidth = row.getKeys().sumOf { it.getWidth() }
            val scaleModifier = emptyDisplayWidth / currentKeysWidth
            var xCoord: Int

            if (row.isBorderToBorder()) {
                // Размещение клавиш от края до края
                xCoord = initialGap

                row.getKeys().forEach { key ->
                    if (orientation != defaultOrientation) {
                        val newKeyWidth = (key.getWidth() * scaleModifier).toInt()
                        if (calculatedCommonKeyWidth == -1) {
                            calculatedCommonKeyWidth = newKeyWidth
                        }

                        key.setX(xCoord)
                        key.setWidth(newKeyWidth)
                        xCoord += newKeyWidth + gap
                    } else key.setDefaultParams()
                }
            } else {
                // Размещение клавиш по центру
                xCoord = ((displayWidth - (row.getKeys().size - 1) * gap - row.getKeys().size
                        * calculatedCommonKeyWidth) / 2.0).toInt()

                row.getKeys().forEach { key ->
                    if (orientation != defaultOrientation) {
                        key.setX(xCoord)
                        key.setWidth(calculatedCommonKeyWidth)

                        xCoord += calculatedCommonKeyWidth + gap
                    } else key.setDefaultParams()
                }
            }
        }
    }

    fun updateDefaultOrientation(orientation: Int) {
        defaultOrientation = orientation
    }

    private fun skipToEndOfRow(parser: XmlResourceParser) {
        while (parser.next() != XmlResourceParser.END_DOCUMENT) {
            if (parser.eventType == XmlResourceParser.END_TAG && parser.name == TAG_ROW) {
                break
            }
        }
    }

    private fun getHorizontalKeyGap(displayWidthDp: Int, initial: Boolean = false): Int {
        return context.resources.getDimensionPixelSize(
            if (initial) {
                if (displayWidthDp >= SCREEN_WIDTH_WHEN_NEED_SCALE_GAP) {
                    R.dimen.key_horizontal_gap_l
                } else {
                    R.dimen.key_horizontal_gap_s
                }
            } else {
                if (displayWidthDp >= SCREEN_WIDTH_WHEN_NEED_SCALE_GAP) {
                    R.dimen.key_horizontal_gap_m
                } else {
                    R.dimen.key_horizontal_gap_s
                }
            }
        )
    }

    companion object {
        const val TAG_KEYBOARD = "Keyboard"
        const val NOT_A_KEY = -1
        private const val SCREEN_WIDTH_WHEN_NEED_SCALE_GAP = 600 // DisplayMetrics.DENSITY_600
    }

    override fun setEnterType(enterType: EnterType) {
        val enterKey = keys.find { it.getCodes()?.first() == KeyTypes.ENTER.code }
        val enterDrawable = ContextCompat.getDrawable(
            context,
            when (enterType) {
                EnterType.GO -> R.drawable.ic_enter_go
                EnterType.DONE -> R.drawable.ic_enter_done
                EnterType.SEARCH -> R.drawable.ic_enter_search
                EnterType.NEXT -> R.drawable.ic_enter_next
            }
        )

        if (enterKey != null && enterDrawable != null) {
            enterKey.setIcon(Icon(enterDrawable, state = IconState.NORMAL, IconAlignment.CENTER))
        }
    }
}
