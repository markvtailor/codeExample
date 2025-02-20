package com.wallpaperscraft.keby.app.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.databinding.LayoutSettingsItemBinding

@SuppressLint("ClickableViewAccessibility")
class SettingsItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: LayoutSettingsItemBinding? = null
    private val binding get() = _binding!!

    init {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.SettingsItemView, defStyleAttr, 0)
        val iconDrawable = attributes.getDrawable(R.styleable.SettingsItemView_settings_icon)
        val labelText: String = attributes.getString(R.styleable.SettingsItemView_settings_title)!!
        val labelTextSize = attributes.getDimension(R.styleable.SettingsItemView_text_size, resources.getDimension(R.dimen.label_text_size))
        val labelTextColor = attributes.getColor(
            R.styleable.SettingsItemView_text_color, ContextCompat.getColor(context, R.color.white)
        )
        val iconTintColor = attributes.getColor(
            R.styleable.SettingsItemView_icon_tint, ContextCompat.getColor(context, R.color.white)
        )
        attributes.recycle()

        _binding = LayoutSettingsItemBinding.inflate(LayoutInflater.from(context), this, true)

        binding.settingsIcon.apply {
            imageTintList = ColorStateList.valueOf(iconTintColor)
            setImageDrawable(iconDrawable)
        }

        binding.settingsLabel.apply {
            text = labelText
            setTextSize(TypedValue.COMPLEX_UNIT_PX, labelTextSize)
            setTextColor(labelTextColor)
        }

        binding.settingsParent.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val color = ContextCompat.getColor(context, R.color.green)
                binding.settingsLabel.setTextColor(color)
                binding.settingsIcon.imageTintList = ColorStateList.valueOf(color)
                binding.divider.dividerColor = color
                performClick()
            }
            if (event.action == MotionEvent.ACTION_UP) {
                binding.settingsIcon.imageTintList = ColorStateList.valueOf(iconTintColor)
                binding.settingsLabel.setTextColor(labelTextColor)
                binding.divider.dividerColor = ContextCompat.getColor(context, R.color.white)
            }
            false
        }
    }

    fun setForwardButtonClick(click: () -> Unit) {
        binding.settingsParent.setOnClickListener {
            click.invoke()
        }
    }

}