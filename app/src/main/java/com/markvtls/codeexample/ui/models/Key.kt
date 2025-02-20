package com.wallpaperscraft.keby.app.ui.models

import android.content.Context
import android.content.res.XmlResourceParser
import android.util.TypedValue
import android.util.Xml
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.utils.getDimensionOrFraction
import com.wallpaperscraft.keby.app.utils.isDomain
import com.wallpaperscraft.keby.app.utils.parseCSV
import com.wallpaperscraft.keby.domain.models.IconAlignment
import com.wallpaperscraft.keby.domain.models.KeyGroup
import com.wallpaperscraft.keby.domain.models.IconState
import com.wallpaperscraft.keby.domain.models.KeyTypes
import org.xmlpull.v1.XmlPullParser

class Key(
    private var x: Int,
    private var y: Int,
    private val parentRow: Row,
    context: Context,
    parser: XmlResourceParser
) {
    private var defaultX: Int = 0
    private var defaultY: Int = 0
    private var width: Int = 0
    private var defaultWidth: Int = 0
    private var height: Int = 0
    private var defaultHeight: Int = 0
    private var verticalGap: Int = 0
    private var horizontalGap: Int = 0
    private var codes = ValueWithAlternative<IntArray>()
    private var label: ValueWithAlternative<CharSequence> = ValueWithAlternative("")
    private var isRepeatable: Boolean = false
    private var isPressed = false
    private val icons: ArrayList<Icon> = arrayListOf()
    private var iconsHorizontalMargin = 0f
    private var iconsState: IconState = IconState.NORMAL
    private var popupKeyboardChars: ValueWithAlternative<List<String>> = ValueWithAlternative()
    private var secondaryLabel: ValueWithAlternative<String> = ValueWithAlternative("")
    private val isPreviewEnabled: Boolean
    private val group: KeyGroup
    private val alternativeValueEnabled: Boolean
    private var isAlternativeValue = false
    private var isSpecialKey = false

    init {
        parser.require(XmlPullParser.START_TAG, null, TAG_KEY)
        val res = context.resources
        var attrs = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.Keyboard)
        width = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyWidth,
            parentRow.getParentKeyboard().getDisplayWidth(),
            parentRow.getParentKeyboard().getKeyWidth()
        )
        height = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyHeightPortrait,
            parentRow.getParentKeyboard().getDisplayHeight(),
            parentRow.getParentKeyboard().getKeyHeight()
        )
        horizontalGap = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyHorizontalGap,
            parentRow.getParentKeyboard().getDisplayWidth(),
            parentRow.getParentKeyboard().getKeyHorizontalGap()
        )
        verticalGap = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyVerticalGap,
            parentRow.getParentKeyboard().getDisplayHeight(),
            parentRow.getParentKeyboard().getKeyVerticalGap()
        )
        this.x += horizontalGap
        this.y += verticalGap
        attrs.recycle()
        attrs = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.Key)
        val keyCodesTypedVal = TypedValue()
        attrs.getValue(R.styleable.Key_codes, keyCodesTypedVal)
        label.value = attrs.getText(R.styleable.Key_keyLabel) ?: ""
        if (keyCodesTypedVal.type == TypedValue.TYPE_INT_DEC ||
            keyCodesTypedVal.type == TypedValue.TYPE_INT_HEX
        ) {
            codes.value = intArrayOf(keyCodesTypedVal.data)
        } else if (keyCodesTypedVal.type == TypedValue.TYPE_STRING) {
            codes.value = keyCodesTypedVal.string.toString().parseCSV()
        }

        if (codes.value == null && label.value != "") {
            codes.value = intArrayOf(label.value!!.first().code)
        } else {
            isSpecialKey = KeyTypes.getKeyType(codes.value!!.first()) != null
        }
        isRepeatable = attrs.getBoolean(R.styleable.Key_isRepeatable, false)
        secondaryLabel.value = attrs.getString(R.styleable.Key_keySecondaryLabel)
        attrs.getText(R.styleable.Key_popupCharacters)?.let { chars ->
            if (chars.isNotBlank()) {
                popupKeyboardChars.value = chars.split(" ")
            }
        }
        alternativeValueEnabled = attrs.getBoolean(R.styleable.Key_alternativeVariantEnabled, false)
        if (alternativeValueEnabled) {
            label.alternativeValue = attrs.getText(R.styleable.Key_alternativeKeyLabel) ?: ""
            codes.alternativeValue = intArrayOf(label.alternativeValue!!.first().code)
            secondaryLabel.alternativeValue =
                attrs.getString(R.styleable.Key_alternativeKeySecondaryLabel)
            attrs.getText(R.styleable.Key_alternativePopupCharacters)?.let { chars ->
                if (chars.isNotBlank()) {
                    popupKeyboardChars.alternativeValue = chars.split(" ")
                }
            }
        }
        isPreviewEnabled = attrs.getBoolean(R.styleable.Key_isPreviewEnabled, true)
        iconsHorizontalMargin =
            attrs.getDimensionPixelSize(R.styleable.Key_iconsHorizontalMargin, 0).toFloat()
        attrs.getDrawable(R.styleable.Key_centerIcon)?.apply {
            icons.add(
                Icon(
                    drawable = this,
                    state = IconState.NORMAL,
                    alignment = IconAlignment.CENTER
                )
            )
        }
        attrs.getDrawable(R.styleable.Key_leftIcon)?.apply {
            icons.add(
                Icon(
                    drawable = this,
                    state = IconState.NORMAL,
                    alignment = IconAlignment.LEFT
                )
            )
        }
        attrs.getDrawable(R.styleable.Key_rightIcon)?.apply {
            icons.add(
                Icon(
                    drawable = this,
                    state = IconState.NORMAL,
                    alignment = IconAlignment.RIGHT
                )
            )
        }
        attrs.getDrawable(R.styleable.Key_centerIconTapped)?.apply {
            icons.add(
                Icon(
                    drawable = this,
                    state = IconState.PRESSED,
                    alignment = IconAlignment.CENTER
                )
            )
        }
        attrs.getDrawable(R.styleable.Key_leftIconTapped)?.apply {
            icons.add(
                Icon(
                    drawable = this,
                    state = IconState.PRESSED,
                    alignment = IconAlignment.LEFT
                )
            )
        }
        attrs.getDrawable(R.styleable.Key_rightIconTapped)?.apply {
            icons.add(
                Icon(
                    drawable = this,
                    state = IconState.PRESSED,
                    alignment = IconAlignment.RIGHT
                )
            )
        }
        attrs.getDrawable(R.styleable.Key_centerIconLocked)?.apply {
            icons.add(
                Icon(
                    drawable = this,
                    state = IconState.LOCKED,
                    alignment = IconAlignment.CENTER
                )
            )
        }
        attrs.getDrawable(R.styleable.Key_leftIconLocked)?.apply {
            icons.add(
                Icon(
                    drawable = this,
                    state = IconState.LOCKED,
                    alignment = IconAlignment.LEFT
                )
            )
        }
        attrs.getDrawable(R.styleable.Key_rightIconLocked)?.apply {
            icons.add(
                Icon(
                    drawable = this,
                    state = IconState.LOCKED,
                    alignment = IconAlignment.RIGHT
                )
            )
        }
        if (codes.value != null && codes.value!!.isEmpty() && !label.value.isNullOrEmpty()) {
            codes.value = intArrayOf(label.value!![0].code)
        }
        group = KeyGroup.getGroup(attrs.getInt(R.styleable.Key_group, 0))
        attrs.recycle()

        initDefaultParams()
    }

    fun setAlternativeValues(enabled: Boolean) {
        if (alternativeValueEnabled) {
            isAlternativeValue = enabled
        }
    }

    fun isAlternativeValue() = isAlternativeValue

    fun isAlternativeValueEnabled() = alternativeValueEnabled

    fun getGroup() = group

    fun getDefaultX() = defaultX

    fun setDefaultX(x: Int) {
        this.x = x
    }

    fun setDefaultY(y: Int) {
        this.y = y
    }

    fun setDefaultHeight(height: Int) {
        this.height = height
    }

    fun getDefaultWidth() = defaultWidth

    fun setDefaultWidth(width: Int) {
        this.width = width
    }

    fun isPreviewEnabled() = isPreviewEnabled

    fun setDefaultParams() {
        width = defaultWidth
        height = defaultHeight
        x = defaultX
        y = defaultY
    }

    fun initDefaultParams() {
        defaultY = y
        defaultX = x
        defaultHeight = height
        defaultWidth = width
    }

    fun setIcon(icon: Icon) {
        icons.find { it.alignment == icon.alignment && it.state == icon.state }?.let { oldIcon ->
            icons.remove(oldIcon)
        }
        icons.add(icon)
    }

    fun getIcons(): List<Icon> {
        return icons
    }

    fun getIconsHorizontalMargin() = iconsHorizontalMargin

    fun getPopupKeyboardChars(): List<String> = if (alternativeValueEnabled && isAlternativeValue) {
        popupKeyboardChars.alternativeValue
    } else {
        popupKeyboardChars.value
    } ?: emptyList()

    fun getSecondaryLabel() = if (alternativeValueEnabled && isAlternativeValue) {
        secondaryLabel.alternativeValue
    } else {
        secondaryLabel.value
    }

    fun getX() = x

    fun setX(value: Int) {
        x = value
    }

    fun getY() = y

    fun setY(value: Int) {
        y = value
    }

    fun getWidth() = width

    fun setWidth(value: Int) {
        width = value
    }

    fun getHeight() = height

    fun setHeight(value: Int) {
        height = value
    }

    fun getParentRow() = parentRow

    fun getHorizontalGap() = horizontalGap

    fun getVerticalGap() = verticalGap

    fun getCodes(): IntArray? {
        val alternative = alternativeValueEnabled && isAlternativeValue

        return if (!alternative) {
            codes.value
        } else {
            codes.alternativeValue
        }
    }

    fun isRepeatable() = isRepeatable

    fun getIconsState() = iconsState

    fun setIconsState(state: IconState) {
        iconsState = state
    }

    fun isPressed() = isPressed

    fun onPressed() {
        isPressed = true
    }

    fun onReleased() {
        isPressed = false
    }

    fun isInside(x: Int, y: Int): Boolean {
        return (x >= this.x - horizontalGap && x <= this.x + width + horizontalGap) && (y >= this.y - verticalGap && y <= this.y + height + verticalGap)
    }

    fun adjustLabelCase(): String {
        val alternative = alternativeValueEnabled && isAlternativeValue
        val result: String =
            (if (alternative) this.label.alternativeValue else this.label.value)?.toString() ?: ""

        return if (result.isNotEmpty() && !result.isDomain() && !isSpecialKey) {
            if (parentRow.getParentKeyboard().isShifted()) {
                result.uppercase()
            } else {
                result.lowercase()
            }
        } else result
    }

    companion object {
        const val TAG_KEY = "Key"

        data class ValueWithAlternative<T>(var value: T? = null, var alternativeValue: T? = null)
    }
}