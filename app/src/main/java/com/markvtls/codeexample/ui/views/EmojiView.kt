package com.wallpaperscraft.keby.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.wallpaperscraft.keby.app.utils.getDefaultEmojiTheme
import com.wallpaperscraft.keby.app.utils.isOrientationLandscape
import com.wallpaperscraft.keby.app.utils.safeParseColor
import com.wallpaperscraft.keby.databinding.ViewEmojiBinding
import com.wallpaperscraft.keby.domain.models.Background
import com.wallpaperscraft.keby.domain.models.EmojiTheme

class EmojiView : FrameLayout {

    private val binding = ViewEmojiBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )
    private var theme: EmojiTheme = getDefaultEmojiTheme()
    private var onPrintEmojiClick: ((emoji: String) -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        binding.emojiPicker.emojiGridColumns = if (context.isOrientationLandscape()) {
            EMOJI_COLUMN_COUNT_LANDSCAPE
        } else {
            EMOJI_COLUMN_COUNT_PORTRAIT
        }
        applyTheme(theme)
        binding.emojiPicker.setOnEmojiPickedListener { emoji ->
            onPrintEmojiClick?.invoke(emoji.emoji)
        }
    }

    fun applyTheme(theme: EmojiTheme) {
        when (val background = theme.background) {
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
    }

    fun setOnPrintEmojiListener(listener: (emoji: String) -> Unit) {
        onPrintEmojiClick = listener
    }

    companion object {
        private const val EMOJI_COLUMN_COUNT_PORTRAIT = 9
        private const val EMOJI_COLUMN_COUNT_LANDSCAPE = 12
    }
}