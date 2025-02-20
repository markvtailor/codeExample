package com.wallpaperscraft.keby.app.ui.shared.themes

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.wallpaperscraft.keby.R

class ButtonStateTheme private constructor(
    @ColorInt val backgroundColor: Int?,
    @ColorInt val textColor: Int?,
    val icon: Drawable?,
    @ColorRes val iconTintResource: Int?
) {

    data class Builder(
        private val context: Context,
        @ColorInt private var backgroundColor: Int? = null,
        @ColorInt private var textColor: Int? = null,
        private var icon: Drawable? = null,
        @ColorRes private var iconTintResource: Int? = null
    ) {
        fun backgroundColor(@ColorInt backgroundColor: Int) = apply { this.backgroundColor = backgroundColor }
        fun textColor(@ColorInt textColor: Int) = apply { this.textColor = textColor }
        fun icon(icon: Drawable) = apply { this.icon = icon }
        fun iconTintResource(@ColorRes iconTintResource: Int) = apply { this.iconTintResource = iconTintResource }
        fun build() = ButtonStateTheme(backgroundColor, textColor, icon, iconTintResource)
        fun buildEnabledButtonTheme() =
            textColor(ContextCompat.getColor(context, R.color.black))
            .backgroundColor(ContextCompat.getColor(context, R.color.green))
            .build()
        fun buildDisableButtonTheme() =
            textColor(ContextCompat.getColor(context, R.color.violet_text))
            .backgroundColor(ContextCompat.getColor(context, R.color.button_disabled_color))
            .icon(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_check, null)!!)
            .iconTintResource(R.color.violet_text)
            .build()

    }
}