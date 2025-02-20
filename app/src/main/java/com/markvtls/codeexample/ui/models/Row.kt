package com.wallpaperscraft.keby.app.ui.models

import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.util.Xml
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.utils.getDimensionOrFraction
import org.xmlpull.v1.XmlPullParser

class Row(private val parent: Keyboard, res: Resources, parser: XmlResourceParser) {
    private val keyboardMode: Int
    private val keys: ArrayList<Key> = ArrayList()
    private var borderToBorder: Boolean = false
    private var keyWidth: Int = 0
    private var keyHeight: Int = 0
    private var keyHorizontalGap: Int = 0
    private var keyVerticalGap: Int = 0

    init {
        parser.require(XmlPullParser.START_TAG, null, TAG_ROW)
        var attrs = res.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.Keyboard)
        keyWidth = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyWidth,
            parent.getDisplayWidth(),
            parent.getKeyWidth()
        )
        keyHeight = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyHeightPortrait,
            parent.getDisplayHeight(),
            parent.getKeyHeight()
        )
        keyHorizontalGap = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyHorizontalGap,
            parent.getDisplayWidth(),
            parent.getKeyHorizontalGap()
        )
        keyVerticalGap = attrs.getDimensionOrFraction(
            R.styleable.Keyboard_keyVerticalGap,
            parent.getDisplayHeight(),
            parent.getKeyVerticalGap()
        )
        attrs.recycle()
        attrs = res.obtainAttributes(
            Xml.asAttributeSet(parser),
            R.styleable.Row
        )
        borderToBorder = attrs.getBoolean(
            R.styleable.Row_borderToBorder,
            false
        )
        keyboardMode = attrs.getResourceId(
            R.styleable.Row_keyboardMode,
            0
        )
        attrs.recycle()
    }

    fun addKey(key: Key) {
        keys.add(key)
    }

    fun getKeys(): List<Key> = keys

    fun isBorderToBorder() = borderToBorder

    fun getParentKeyboard() = parent

    fun getKeyHeight() = keyHeight

    fun getKeyWidth() = keyWidth

    fun setKeyWidth(width: Int) {
        keyWidth = width
    }

    fun getKeyVerticalGap() = keyVerticalGap

    fun getKeyHorizontalGap() = keyHorizontalGap

    fun setHorizontalGap(gap: Int) {
        keyHorizontalGap = gap
    }

    fun getKeyboardMode() = keyboardMode

    companion object {
        const val TAG_ROW = "Row"
    }
}