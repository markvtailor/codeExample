package com.wallpaperscraft.keby.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.databinding.LayoutSettingsSwitchExtendedBinding

class SettingsSwitchExtendedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: LayoutSettingsSwitchExtendedBinding? = null
    private val binding get() = _binding!!


    init {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.SettingsSwitchExtendedView, defStyleAttr, 0
        )
        val settingsLabel =
            attributes.getString(R.styleable.SettingsSwitchExtendedView_settings_label)
        val settingsDescription =
            attributes.getString(R.styleable.SettingsSwitchExtendedView_settings_description)
        val labelTextSize = attributes.getDimension(
            R.styleable.SettingsSwitchExtendedView_label_text_size,
            resources.getDimension(R.dimen.settings_label_text_size)
        )
        val descriptionTextSize = attributes.getDimension(
            R.styleable.SettingsSwitchExtendedView_description_text_size, resources.getDimension(R.dimen.settings_description_text_size)
        )
        attributes.recycle()

        _binding =
            LayoutSettingsSwitchExtendedBinding.inflate(LayoutInflater.from(context), this, true)
        binding.settingsLabel.text = settingsLabel
        binding.settingsLabel.apply {
            text = settingsLabel
            setTextSize(TypedValue.COMPLEX_UNIT_PX, labelTextSize)
        }
        if (settingsDescription == null) {
            binding.settingsDescription.visibility = View.GONE
        } else settingsDescription.let {
            binding.settingsDescription.apply {
                text = it
                setTextSize(TypedValue.COMPLEX_UNIT_PX, descriptionTextSize)
            }
        }

    }


    fun setOnSwitchChangeListener(listener: (Boolean) -> Unit) {
        binding.settingsSwitch.setOnCheckedChangeListener { _, isChecked ->
            listener(isChecked)
        }
    }

    fun setInitialState(state: Boolean) {
        binding.settingsSwitch.isChecked = state
        binding.settingsSwitch.jumpDrawablesToCurrentState()
    }

    fun setState(state: Boolean) {
        binding.settingsSwitch.isChecked = state
    }

}