package com.wallpaperscraft.keby.app.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.utils.createColorStateList
import com.wallpaperscraft.keby.app.utils.fromDpToPx
import com.wallpaperscraft.keby.app.utils.getDefaultClipboardTheme
import com.wallpaperscraft.keby.app.utils.safeParseColor
import com.wallpaperscraft.keby.databinding.ItemClipboardBinding
import com.wallpaperscraft.keby.databinding.ViewClipboardBinding
import com.wallpaperscraft.keby.domain.models.Background
import com.wallpaperscraft.keby.domain.models.ClipboardItem
import com.wallpaperscraft.keby.domain.models.ClipboardTheme
import com.wallpaperscraft.keby.domain.models.Color

class ClipboardView : FrameLayout {

    private val binding = ViewClipboardBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )
    private var theme: ClipboardTheme = getDefaultClipboardTheme()
    private val clipboardAdapter = ClipboardAdapter()
    private val clipboardDecorator = ClipboardDecorator()
    private var textColorStateList: ColorStateList = createColorStateList()
    private var deleteColorStateList: ColorStateList = createColorStateList()
    private var itemBackgroundColorStateList: ColorStateList = createColorStateList()
    private var clipboardTextSize = DEFAULT_TEXT_SIZE
    private var clipboardListener: ClipboardItemListener? = null

    init {
        applyTheme(theme)
        binding.clipboardList.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = clipboardAdapter
            addItemDecoration(clipboardDecorator)
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setClipboardItemListener(listener: ClipboardItemListener) {
        clipboardListener = listener
    }

    fun setClipboardItems(items: List<ClipboardItem>) {
        val isItemsNotEmpty = items.isNotEmpty()
        clipboardAdapter.setItems(items)
        binding.clipboardList.isVisible = isItemsNotEmpty
        binding.noItemsText.isVisible = !isItemsNotEmpty
    }

    fun applyTheme(clipboardTheme: ClipboardTheme) {
        when (val background = clipboardTheme.background) {
            is Background.ColorBackground -> {
                binding.root.setBackgroundColor(
                    safeParseColor(background.color.defaultHex)
                )
            }

            is Background.DrawableBackground -> {
                binding.root.background = ContextCompat.getDrawable(
                    context,
                    background.defaultRes
                )
            }
        }

        when (val itemBackground = clipboardTheme.itemsTheme.itemBackground) {
            is Background.ColorBackground -> {
                itemBackgroundColorStateList = when (val color = itemBackground.color) {
                    is Color.ColorWithState -> {
                        createColorStateList(
                            defaultColorHex = color.defaultHex,
                            tappedColorHex = color.tappedHex
                        )
                    }

                    is Color.ColorWithoutState -> {
                        createColorStateList(defaultColorHex = color.defaultHex)
                    }
                }
            }

            else -> Unit
        }
        textColorStateList = when (val color = clipboardTheme.itemsTheme.textColor) {
            is Color.ColorWithState -> {
                createColorStateList(
                    defaultColorHex = color.defaultHex,
                    tappedColorHex = color.tappedHex
                )
            }
            is Color.ColorWithoutState -> {
                createColorStateList(defaultColorHex = color.defaultHex)
            }
        }
        deleteColorStateList = when (val color = clipboardTheme.itemsTheme.deleteIconColor) {
            is Color.ColorWithState -> {
                createColorStateList(
                    defaultColorHex = color.defaultHex,
                    tappedColorHex = color.tappedHex
                )
            }

            is Color.ColorWithoutState -> {
                createColorStateList(
                    defaultColorHex = color.defaultHex
                )
            }
        }
    }

    private inner class ClipboardDecorator : RecyclerView.ItemDecoration() {

        private var itemToItemMargins =
            resources.getDimensionPixelSize(R.dimen.clipboard_item_vertical_margins)
        private var recyclerMargins =
            resources.getDimensionPixelSize(R.dimen.clipboard_recycler_margins)

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val lastPosition = clipboardAdapter.itemCount - 1

            outRect.apply {
                top = if (position == 0) recyclerMargins else itemToItemMargins
                bottom = if (position == lastPosition) recyclerMargins else itemToItemMargins
                right = recyclerMargins
                left = recyclerMargins
            }
        }
    }

    private inner class ClipboardAdapter : RecyclerView.Adapter<ClipboardAdapter.ViewHolder>() {

        private val items: ArrayList<ClipboardItem> = arrayListOf()

        @SuppressLint("NotifyDataSetChanged")
        fun setItems(clipboardItems: List<ClipboardItem>) {
            items.apply {
                clear()
                addAll(clipboardItems)
            }

            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemClipboardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]

            holder.binding.apply {
                clipboardListener?.let { listener ->
                    root.setOnClickListener { listener.onTextClick(item) }
                    pinIcon.setOnClickListener { listener.onPinClick(item) }
                    if (!item.pinned) {
                        deleteIcon.setOnClickListener { listener.onDeleteClick(item) }
                    }
                }

                pinIcon.setImageResource(
                    if (!item.pinned) R.drawable.ic_pin else R.drawable.ic_pinned
                )

                when (val itemBackground = theme.itemsTheme.itemBackground) {
                    is Background.ColorBackground -> {
                        root.background = PaintDrawable().apply {
                            setCornerRadius(itemBackground.cornerRadius.fromDpToPx(context))
                        }
                        theme.itemsTheme.itemBackground
                        root.backgroundTintList = itemBackgroundColorStateList
                    }

                    is Background.DrawableBackground -> {
                        root.background = ContextCompat.getDrawable(
                            context,
                            itemBackground.defaultRes
                        )
                    }
                }

                clipboardText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, clipboardTextSize)
                clipboardText.setTextColor(textColorStateList)
                clipboardText.text = item.text
                pinIcon.setColorFilter(safeParseColor(theme.itemsTheme.pinIconColor.defaultHex))
                deleteIcon.imageTintList = deleteColorStateList
                deleteIcon.isVisible = !item.pinned
            }
        }

        private inner class ViewHolder(val binding: ItemClipboardBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    interface ClipboardItemListener {
        fun onDeleteClick(item: ClipboardItem)
        fun onPinClick(item: ClipboardItem)
        fun onTextClick(item: ClipboardItem)
    }

    companion object {
        private const val DEFAULT_TEXT_SIZE = 12f
    }
}