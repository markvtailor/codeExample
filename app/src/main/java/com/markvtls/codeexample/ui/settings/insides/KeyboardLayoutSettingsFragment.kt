package com.wallpaperscraft.keby.app.ui.settings.insides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.settings.SettingsViewModel
import com.wallpaperscraft.keby.databinding.FragmentSettingsKeyboardLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KeyboardLayoutSettingsFragment: Fragment(R.layout.fragment_settings_keyboard_layout) {

    private var _binding: FragmentSettingsKeyboardLayoutBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsKeyboardLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.apply {
            settingsDigitsRow.apply {
                setOnSwitchChangeListener { boolean ->
                    settingsViewModel.setNumericRowKeyboardEnabled(boolean)
                }
                setInitialState(settingsViewModel.isNumericRowKeyboardEnabled())
            }

            settingsSuggestionsRow.apply {
                setOnSwitchChangeListener { boolean ->
                    settingsViewModel.updateSuggestionsRowSettingsValue(boolean)
                }
                setInitialState(settingsViewModel.isSuggestionsRowEnabled())
            }

            settingsAlternateSymbols.apply {
                setOnSwitchChangeListener { boolean ->
                    settingsViewModel.setAlternativeSymbolicKeyboardEnabled(boolean)
                }
                setInitialState(settingsViewModel.isAlternativeSymbolicKeyboardEnabled())
            }
        }
    }

}