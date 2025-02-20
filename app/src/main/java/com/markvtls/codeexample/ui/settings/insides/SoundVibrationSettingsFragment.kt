package com.wallpaperscraft.keby.app.ui.settings.insides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.slider.Slider
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.settings.SettingsViewModel
import com.wallpaperscraft.keby.app.ui.views.SettingsSwitchExtendedView
import com.wallpaperscraft.keby.databinding.FragmentSettingsSoundVibrationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundVibrationSettingsFragment : Fragment(R.layout.fragment_settings_sound_vibration) {

    private var _binding: FragmentSettingsSoundVibrationBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsSoundVibrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.apply {
            settingsClickSound.apply {
                setOnSwitchChangeListener { boolean ->
                    settingsViewModel.updateClickSoundSettingsValue(boolean)
                }
                setInitialState(settingsViewModel.isClickSoundEnabled())
            }

            settingsClickVibration.apply {
                setOnSwitchChangeListener { boolean ->
                    settingsViewModel.updateClickVibrationSettingsValue(boolean)
                }
                setInitialState(settingsViewModel.isClickVibrationEnabled())
            }
        }

        binding.soundSlider.apply {
            value = settingsViewModel.getClickSoundVolume()
            addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(p0: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                   settingsViewModel.updateClickSoundVolumeSettingsValue(slider.value)
                    handleSliderChange(slider.value, binding.settingsClickSound)
                }
            })
        }

        binding.vibrationSlider.apply {
            value = settingsViewModel.getClickVibrationVolume()
            addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(p0: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                   settingsViewModel.updateClickVibrationVolumeSettingsValue(slider.value)
                    handleSliderChange(slider.value, binding.settingsClickVibration)
                }
            })
        }

    }

    fun handleSliderChange(sliderValue: Float, switch: SettingsSwitchExtendedView) {
        if (sliderValue > 0F) {
            switch.setState(true)
        } else {
            switch.setState(false)
        }
    }
}