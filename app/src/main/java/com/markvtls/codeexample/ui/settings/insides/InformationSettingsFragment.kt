package com.wallpaperscraft.keby.app.ui.settings.insides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.databinding.FragmentSettingsInformationBinding

class InformationSettingsFragment: Fragment(R.layout.fragment_settings_information) {

    private var _binding: FragmentSettingsInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsInformationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.userAgreementTitle.setOnClickListener {
            val action = InformationSettingsFragmentDirections.actionInformationSettingsFragmentToWebViewFragment2(resources.getString(R.string.url_user_agreement))
            findNavController().navigate(action)
        }

        binding.privacyPolicyTitle.setOnClickListener {
            val action = InformationSettingsFragmentDirections.actionInformationSettingsFragmentToWebViewFragment2(resources.getString(R.string.url_privacy_policy))
            findNavController().navigate(action)
        }
    }

}