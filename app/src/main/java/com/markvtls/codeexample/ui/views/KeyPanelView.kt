package com.wallpaperscraft.keby.app.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.models.KeyIcon
import com.wallpaperscraft.keby.app.ui.models.PopupItemIcon
import com.wallpaperscraft.keby.app.ui.models.Key
import com.wallpaperscraft.keby.app.utils.ALPHA_MAX
import com.wallpaperscraft.keby.app.utils.fromDpToPx
import com.wallpaperscraft.keby.app.utils.getDefaultSelectablePopUpTheme
import com.wallpaperscraft.keby.app.utils.getLocale
import com.wallpaperscraft.keby.app.utils.isDomain
import com.wallpaperscraft.keby.app.utils.isOrientationLandscape
import com.wallpaperscraft.keby.app.utils.isPunctuation
import com.wallpaperscraft.keby.app.utils.safeGetDrawable
import com.wallpaperscraft.keby.app.utils.safeParseColor
import com.wallpaperscraft.keby.domain.models.Background
import com.wallpaperscraft.keby.domain.models.BackgroundStyle
import com.wallpaperscraft.keby.domain.models.Color
import com.wallpaperscraft.keby.domain.models.KeyPopupDirection
import com.wallpaperscraft.keby.domain.models.PopupItem
import com.wallpaperscraft.keby.domain.models.PopupItemChar
import com.wallpaperscraft.keby.domain.models.SelectableKeyPopupTheme

class KeyPanelView : View {

    private val paint = Paint()
    private val items = ArrayList<PopupItem>()
    private var canvas: Canvas? = null
    private var theme: SelectableKeyPopupTheme = getDefaultSelectablePopUpTheme()
    private var lastPointerX = 0f
    private var lastPointerY = 0f
    private var lastPointerCount = 1
    private var currentItemIndex = NOT_AN_ITEM
    private var abortItem = false
    private var listener: SelectablePopupOnItemClickListener? = null
    private var popupTextSize = DEFAULT_TEXT_SIZE
    private var rowCount = DEFAULT_ROW_COUNT
    private var itemsInRow = DEFAULT_ITEMS_IN_ROW_COUNT
    private var itemWidth = DEFAULT_ITEM_WIDTH
    private var itemHeight = DEFAULT_ITEM_HEIGHT

    init {
        applyBackground()
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (items.isNotEmpty()) {
            val newHeight: Int = getPanelHeight()
            val newWidth: Int = getPanelWidth()
            setMeasuredDimension(newWidth, newHeight)
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        this.canvas = canvas

        if (items.isNotEmpty()) items.forEach(::drawItem)
    }

    private fun invalidateItem(itemIndex: Int) {
        if (items.isNotEmpty() && !(itemIndex < 0 || itemIndex >= items.size)) {
            val item = items[itemIndex]
            if (canvas != null) drawItem(item)
            postInvalidate(item.x, item.y, item.x + item.width, item.y + item.height)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerCount = event.pointerCount
        var result: Boolean
        if (pointerCount == lastPointerCount) {
            if (pointerCount == 1) {
                result = handleTouchEvent(event)
                lastPointerX = event.x
                lastPointerY = event.y
            } else {
                result = true
            }
        } else {
            if (pointerCount == 1) {
                val down = MotionEvent.obtain(
                    event.eventTime,
                    event.eventTime,
                    MotionEvent.ACTION_DOWN,
                    event.x,
                    event.y,
                    event.metaState
                )
                result = handleTouchEvent(down)
                down.recycle()
                if (event.action == MotionEvent.ACTION_UP) {
                    result = handleTouchEvent(event)
                }
            } else {
                val up = MotionEvent.obtain(
                    event.eventTime,
                    event.eventTime,
                    MotionEvent.ACTION_UP,
                    lastPointerX,
                    lastPointerY,
                    event.metaState
                )
                result = handleTouchEvent(up)
                up.recycle()
            }
        }
        if (event.action == MotionEvent.ACTION_UP) {
            performClick()
        }
        lastPointerCount = pointerCount
        return result
    }

    private fun getCurrentItemIndex(touchX: Int, touchY: Int): Int {
        val item = items.find { it.isInside(touchX, touchY) }

        return item?.let { items.indexOf(it) } ?: NOT_AN_ITEM
    }

    private fun handleTouchEvent(event: MotionEvent): Boolean {
        if (items.isEmpty()) return false

        if (abortItem && event.action != MotionEvent.ACTION_DOWN &&
            event.action != MotionEvent.ACTION_CANCEL
        ) {
            return true
        }
        val touchX = (event.x - paddingLeft).toInt()
        val touchY = (event.y - paddingTop).toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                abortItem = false
                invalidateLastItem()
                currentItemIndex = getCurrentItemIndex(touchX, touchY)

                if (currentItemIndex != NOT_AN_ITEM) {
                    items[currentItemIndex].apply {
                        pressed = true
                    }
                    invalidateItem(currentItemIndex)
                }
            }

            MotionEvent.ACTION_MOVE -> {}
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                abortItem = true
                if (currentItemIndex != NOT_AN_ITEM) {
                    val item = this.items[currentItemIndex].apply {
                        pressed = false
                    }
                    invalidateLastItem()


                    when (item) {
                        is PopupItemChar -> listener?.onCharClick(item.charSequence)
                        is PopupItemIcon -> listener?.onIconClick(item.icon)
                    }
                }
            }
        }
        return true
    }

    private fun invalidateLastItem() {
        if (currentItemIndex != NOT_AN_ITEM) {
            items[currentItemIndex].apply {
                pressed = false
            }
            invalidateItem(currentItemIndex)
        }
    }

    private fun drawItem(item: PopupItem) {
        paint.reset()
        canvas!!.apply {
            save()
            translate(item.x.toFloat(), item.y.toFloat())
        }
        when (val background = theme.keyBackground) {
            is Background.ColorBackground -> {
                val cornerRadius = background.cornerRadius.fromDpToPx(context)

                when (background.style) {
                    BackgroundStyle.Fill -> {
                        drawFillBackground(
                            item = item,
                            background = background,
                            cornerRadius = cornerRadius
                        )
                    }

                    is BackgroundStyle.Stroke -> {
                        drawStrokeBackground(
                            item = item,
                            background = background,
                            cornerRadius = cornerRadius
                        )
                    }

                    is BackgroundStyle.FillAndStroke -> {
                        drawFillBackground(
                            item = item,
                            background = background,
                            cornerRadius = cornerRadius
                        )
                        drawStrokeBackground(
                            item = item,
                            background = background,
                            cornerRadius = cornerRadius
                        )
                    }
                }
            }

            is Background.DrawableBackground -> {
                ContextCompat.getDrawable(
                    context,
                    if (!item.pressed) background.defaultRes else background.tappedRes
                )?.apply {
                    state = drawableState
                    if (item.width != bounds.right || item.height != bounds.bottom) {
                        setBounds(0, 0, item.width, item.height)
                    }
                    draw(canvas!!)
                }
            }
        }

        when (item) {
            is PopupItemChar -> {
                val centerX = item.width / 2F
                val centerY = item.height / 2F
                val chars  = item.charSequence
                if (chars.isNotEmpty()) {
                    paint.apply {
                        reset()
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                        isAntiAlias = true
                        textAlign = Paint.Align.CENTER
                        alpha = ALPHA_MAX
                        textSize = popupTextSize.fromDpToPx(context)
                        color = safeParseColor(
                            when (val color = theme.textColor) {
                                is Color.ColorWithState -> {
                                    if (!item.pressed) {
                                        color.defaultHex
                                    } else {
                                        color.tappedHex
                                    }
                                }

                                is Color.ColorWithoutState -> {
                                    color.defaultHex
                                }
                            }
                        )
                    }

                    when {
                        chars.isPunctuation() -> {
                            paint.textSize *= KeyboardView.TEXT_SIZE_PUNCTUATION_SCALE
                        }
                        chars.isDomain() -> {
                            paint.textSize *= TEXT_SIZE_CHAR_SEQUENCE_SCALE
                        }
                    }

                    canvas!!.drawText(
                        chars,
                        centerX,
                        centerY - (paint.descent() + paint.ascent()) / 2F,
                        paint
                    )
                }
            }

            is PopupItemIcon -> {
                context.safeGetDrawable(item.icon.res).apply {
                    val centerY = itemHeight / 2
                    val centerX = itemWidth / 2
                    val centerIconX = item.iconWidth.fromDpToPx(context) / 2
                    val centerIconY = item.iconHeight.fromDpToPx(context) / 2
                    setBounds(
                        centerX - centerIconX,
                        centerY - centerIconY,
                        centerX + centerIconX,
                        centerY + centerIconY
                    )
                    colorFilter = PorterDuffColorFilter(
                        safeParseColor(
                            when (val color = theme.textColor) {
                                is Color.ColorWithState -> {
                                    if (!item.pressed) {
                                        color.defaultHex
                                    } else {
                                        color.tappedHex
                                    }
                                }

                                is Color.ColorWithoutState -> {
                                    color.defaultHex
                                }
                            }
                        ),
                        PorterDuff.Mode.SRC_IN
                    )
                }.draw(canvas!!)
            }
        }
        canvas!!.restore()
    }

    fun setTheme(theme: SelectableKeyPopupTheme) {
        this.theme = theme
        applyBackground()
        requestLayout()
    }

    fun setOnSelectItemListener(listener: SelectablePopupOnItemClickListener) {
        this.listener = listener
    }

    /**
     * Для просчета основных параметров вьюхи эта функция должна использоваться в первую очередь
     * До вызова initItems()
     */
    fun calculatePanelParameters(parentViewWidth: Int, key: Key): KeyPopupDirection {
        itemsInRow = key.getPopupKeyboardChars().size
        val isLandscape = context.isOrientationLandscape()
        itemWidth = if (!isLandscape) {
            theme.width?.fromDpToPx(context)?.toInt() ?: key.getWidth()
        } else key.getWidth()
        itemHeight = if (!isLandscape) {
            theme.height?.fromDpToPx(context)?.toInt() ?: key.getHeight()
        } else key.getHeight()
        var direction = KeyPopupDirection.CENTER
        var panelWidth = getPanelWidth()
        var rowsCalculated = false
        val widthDifference = (itemWidth - key.getWidth()) / 2

        while (!rowsCalculated) {
            when {
                itemsInRow == 1 && (key.getX() - widthDifference > 0 && key.getX() + key.getWidth() + widthDifference < parentViewWidth) -> {
                    direction = KeyPopupDirection.CENTER
                    rowsCalculated = true
                }

                parentViewWidth - key.getX() > panelWidth -> {
                    //Показ окна от key.x
                    direction = KeyPopupDirection.LEFT_TO_RIGHT
                    rowsCalculated = true
                }

                key.getX() > panelWidth -> {
                    // Показ окна от x = key.x + key.width
                    direction = KeyPopupDirection.RIGHT_TO_LEFT
                    rowsCalculated = true
                }

                else -> {
                    // Нет места, добавить строку
                    rowCount++

                    itemsInRow = key.getPopupKeyboardChars().size / rowCount
                    if (key.getPopupKeyboardChars().size % rowCount != 0) {
                        itemsInRow++
                    }

                    panelWidth = getPanelWidth()
                }
            }
        }

        return direction
    }

    fun initItems(key: Key, shifted: Boolean) {
        val itemToItemMargin = resources.getDimension(R.dimen.key_panel_item_to_item_margin).toInt()
        var itemY: Int = 0 + paddingTop
        var itemX = 0 + paddingLeft

        items.clear()

        val rows = key.getPopupKeyboardChars()
            .chunked(itemsInRow)

        rows.forEachIndexed { rowIndex, popupItems ->
            if (rowIndex != 0) {
                itemY += itemToItemMargin + itemHeight
            }

            popupItems.forEachIndexed { charIndex, chars ->
                val icon = KeyIcon.values().find { it.name == chars }
                val item: PopupItem = if (icon == null) {
                    PopupItemChar(
                        charSequence = if (shifted && !chars.isDomain()) {
                            chars.uppercase(context.getLocale())
                        } else chars,
                        width = itemWidth,
                        height = itemHeight,
                        y = itemY,
                        x = itemX
                    )
                } else {
                    PopupItemIcon(
                        icon = icon,
                        width = itemWidth,
                        height = itemHeight,
                        y = itemY,
                        x = itemX,
                        iconWidth = DEFAULT_ICON_WIDTH,
                        iconHeight = DEFAULT_ICON_HEIGHT
                    )
                }
                items.add(item)
                itemX += itemWidth + if (charIndex != popupItems.indices.last) itemToItemMargin else 0
            }

            itemX = 0 + paddingLeft
        }

        requestLayout()
    }

    fun removeChars() {
        canvas = null
        currentItemIndex = NOT_AN_ITEM
        lastPointerCount = 1
        lastPointerX = 0f
        lastPointerY = 0f
        abortItem = false
        rowCount = DEFAULT_ROW_COUNT
        itemsInRow = DEFAULT_ITEMS_IN_ROW_COUNT
        itemWidth = DEFAULT_ITEM_WIDTH
        itemHeight = DEFAULT_ITEM_HEIGHT
        items.clear()
    }

    fun getPanelHeight(): Int {
        return itemHeight * rowCount + (rowCount - 1) * resources.getDimension(R.dimen.key_panel_item_to_item_margin)
            .toInt() + paddingTop + paddingBottom
    }

    fun getPanelWidth(): Int {
        return itemWidth * itemsInRow + (itemsInRow - 1) * resources.getDimension(R.dimen.key_panel_item_to_item_margin)
            .toInt() + paddingLeft + paddingRight
    }

    private fun drawFillBackground(
        item: PopupItem,
        background: Background.ColorBackground,
        cornerRadius: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.color = safeParseColor(
            when (val color = background.color) {
                is Color.ColorWithState -> {
                    if (!item.pressed) {
                        color.defaultHex
                    } else {
                        color.tappedHex
                    }
                }

                is Color.ColorWithoutState -> {
                    color.defaultHex
                }
            }
        )

        canvas!!.drawRoundRect(
            RectF(
                0f,
                0f,
                item.width.toFloat(),
                item.height.toFloat()
            ),
            cornerRadius,
            cornerRadius,
            paint
        )
    }

    private fun drawStrokeBackground(
        item: PopupItem,
        background: Background.ColorBackground,
        cornerRadius: Float
    ) {
        val strokeWidth = resources.getDimension(R.dimen.default_key_stroke_width)
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.color = safeParseColor(
            when (val color = background.color) {
                is Color.ColorWithState -> {
                    if (!item.pressed) {
                        color.defaultHex
                    } else {
                        color.tappedHex
                    }
                }

                is Color.ColorWithoutState -> {
                    color.defaultHex
                }
            }
        )
        canvas!!.drawRoundRect(
            RectF(
                0f + strokeWidth / 2,
                0f + strokeWidth / 2,
                item.width.toFloat() - strokeWidth / 2,
                item.height.toFloat() - strokeWidth / 2
            ),
            cornerRadius,
            cornerRadius,
            paint
        )
    }

    private fun applyBackground() {
        background = when (val customBackground: Background = theme.background) {
            is Background.ColorBackground -> {
                PaintDrawable(safeParseColor(customBackground.color.defaultHex)).apply {
                    setCornerRadius(customBackground.cornerRadius.fromDpToPx(context))
                }
            }

            is Background.DrawableBackground -> {
                ResourcesCompat.getDrawable(resources, customBackground.defaultRes, null)
            }
        }
    }

    companion object {
        private const val NOT_AN_ITEM = -1
        private const val DEFAULT_ROW_COUNT = 1
        private const val DEFAULT_ITEMS_IN_ROW_COUNT = 0
        private const val DEFAULT_ITEM_WIDTH = 0
        private const val DEFAULT_ITEM_HEIGHT = 0
        private const val DEFAULT_ICON_WIDTH = 20 //Dp
        private const val DEFAULT_ICON_HEIGHT = 20 //Dp
        private const val DEFAULT_TEXT_SIZE = 24f
        private const val TEXT_SIZE_CHAR_SEQUENCE_SCALE = 0.7f

        interface SelectablePopupOnItemClickListener {
            fun onCharClick(text: String)

            fun onIconClick(icon: KeyIcon)
        }
    }
}