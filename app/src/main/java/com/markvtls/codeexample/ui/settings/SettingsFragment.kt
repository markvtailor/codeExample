package com.wallpaperscraft.keby.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.databinding.FragmentSettingsBinding

class SettingsFragment:Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        val navController = findNavController()

        with(binding) {
            settingsTextInput.setForwardButtonClick {
                val action = SettingsFragmentDirections.actionSettingsFragmentToTextInputSettingsFragment()
                navController.navigate(action)
            }
            settingsKeyboardLayout.setForwardButtonClick {
                val action = SettingsFragmentDirections.actionSettingsFragmentToKeyboardLayoutSettingsFragment()
                navController.navigate(action)
            }
            settingsLanguage.setForwardButtonClick {
                val action = SettingsFragmentDirections.actionSettingsFragmentToLanguageSettingsFragment()
                navController.navigate(action)
            }
            settingsSoundVibration.setForwardButtonClick {
                val action = SettingsFragmentDirections.actionSettingsFragmentToSoundVibrationSettingsFragment()
                navController.navigate(action)
            }
            settingsInformation.setForwardButtonClick {
                val action = SettingsFragmentDirections.actionSettingsFragmentToInformationSettingsFragment()
                navController.navigate(action)
            }
            settingsSupport.setForwardButtonClick {
                composeEmail(resources.getString(R.string.email_support))
            }
        }
    }

    private fun composeEmail(addresses: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(addresses))
        }
        startActivity(intent)
    }

}