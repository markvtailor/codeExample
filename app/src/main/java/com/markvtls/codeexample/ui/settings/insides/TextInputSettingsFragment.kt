package com.wallpaperscraft.keby.app.ui.settings.insides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.settings.SettingsViewModel
import com.wallpaperscraft.keby.databinding.FragmentSettingsTextInputBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextInputSettingsFragment: Fragment(R.layout.fragment_settings_text_input) {

    private var _binding: FragmentSettingsTextInputBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsTextInputBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.apply {
            settingsAutocorrect.apply {
                setOnSwitchChangeListener { boolean ->
                    settingsViewModel.updateAutocorrectSettingsValue(boolean)
                }
                setInitialState(settingsViewModel.isAutocorrectEnabled())
            }

            settingsAutocaps.apply {
                setOnSwitchChangeListener { boolean ->
                    settingsViewModel.updateAutocapsSettingsValue(boolean)
                }
                setInitialState(settingsViewModel.isAutocapsEnabled())
            }

            settingsAutospace.apply {
                setOnSwitchChangeListener { boolean ->
                    settingsViewModel.updateAutospaceSettingsValue(boolean)
                }
                setInitialState(settingsViewModel.isAutospaceEnabled())
            }

        }

    }
}