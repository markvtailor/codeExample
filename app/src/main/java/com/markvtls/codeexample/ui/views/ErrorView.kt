package com.wallpaperscraft.keby.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.databinding.LayoutErrorBinding

class ErrorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: LayoutErrorBinding? = null
    private val binding get() = _binding!!

    init {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.ErrorView, defStyleAttr, 0)
        val iconType = IconType.values()[attributes.getInt(R.styleable.ErrorView_ev_icon, 0)]
        attributes.recycle()

        _binding = LayoutErrorBinding.inflate(LayoutInflater.from(context), this, true)

        binding.errorIcon.apply {
            if (iconType == IconType.NONE) {
                isVisible = false
            } else {
                setImageDrawable(
                    VectorDrawableCompat.create(resources, iconType.drawableId, null)!!.mutate()
                )
            }
        }
    }


    fun setErrorMessageText(res: Int) = binding.errorMessage.setText(res)

    fun setErrorMessageRetryButtonClick(click: (View) -> Unit) {
        binding.buttonErrorRetry.setOnClickListener(click)
    }

    companion object {
        private enum class IconType(val drawableId: Int) {
            STANDARD(R.drawable.ic_error), NONE(0)
        }
    }
}