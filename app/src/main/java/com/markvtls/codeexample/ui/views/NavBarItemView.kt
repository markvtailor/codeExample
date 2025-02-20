package com.wallpaperscraft.keby.app.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.databinding.LayoutNavBarItemBinding

class NavBarItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: LayoutNavBarItemBinding? = null
    private val binding get() = _binding!!

    init {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.NavBarItemView, defStyleAttr, 0)
        val navItemIcon = attributes.getDrawable(R.styleable.NavBarItemView_nav_icon)
        val navItemIconColor = attributes.getColor(
            R.styleable.NavBarItemView_nav_icon_color, ContextCompat.getColor(context, R.color.white)
        )
        val navItemLabel = attributes.getString(R.styleable.NavBarItemView_nav_label)
        val navItemIconSize = attributes.getDimension(
            R.styleable.NavBarItemView_nav_icon_size,
            resources.getDimension(R.dimen.nav_bar_icon_small)
        )
        val navItemBackgroundColor = attributes.getColor(
            R.styleable.NavBarItemView_nav_item_background_color, ContextCompat.getColor(context, R.color.green)
        )
        attributes.recycle()

        _binding = LayoutNavBarItemBinding.inflate(LayoutInflater.from(context), this, true)

        setNavItemBackgroundColor(navItemBackgroundColor)

        setNavIconSize(navItemIconSize)

        setNavItemIconColor(navItemIconColor)

        binding.navBarItemIcon.setImageDrawable(navItemIcon)

        binding.navBarItemText.apply {
            text = navItemLabel
        }
    }

    fun setNavItemBackgroundColor(color: Int) {
        binding.navBarItem.setCardBackgroundColor(color)
    }

    fun setNavIconSize(size: Float) {
        binding.navBarItemIcon.layoutParams.apply {
            height = size.toInt()
            width = size.toInt()
        }
        binding.navBarItemIcon.requestLayout()
    }

    fun setNavItemIconColor(color: Int) {
        binding.navBarItemIcon.imageTintList = ColorStateList.valueOf(color)
    }
}