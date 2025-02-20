package com.wallpaperscraft.keby.app.ui.details

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.models.Theme
import com.wallpaperscraft.keby.app.ui.shared.adapters.ThemesFeedAdapter
import com.wallpaperscraft.keby.app.ui.shared.fragments.BaseDialogFragment
import com.wallpaperscraft.keby.app.utils.getDefaultDrawable
import com.wallpaperscraft.keby.app.utils.getThemesDebugList
import com.wallpaperscraft.keby.app.ui.shared.fragments.BaseDialogFragment.Companion.DIALOG_TYPE
import com.wallpaperscraft.keby.app.ui.shared.fragments.BaseDialogFragment.Companion.TAG
import com.wallpaperscraft.keby.app.ui.shared.themes.ButtonStateTheme
import com.wallpaperscraft.keby.databinding.FragmentDetailsBinding


class DetailsFragment: Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val detailsFragmentArgs: DetailsFragmentArgs by navArgs()

    private val detailsViewModel: DetailsViewModel by viewModels()

    private var enabledButtonTheme: ButtonStateTheme? = null
    private var disabledButtonTheme: ButtonStateTheme? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.themeImage.setImageDrawable(getDefaultDrawable())
        enabledButtonTheme = ButtonStateTheme.Builder(requireContext()).buildEnabledButtonTheme()
        disabledButtonTheme = ButtonStateTheme.Builder(requireContext()).buildDisableButtonTheme()

        binding.setThemeButton.setOnClickListener {
            setTheme()
        }

        adaptForThemeState()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adaptForThemeState() {
        val themeState = detailsViewModel.checkThemeState(detailsFragmentArgs.themeId)

        when(themeState) {
            DetailsViewModel.ThemeState.NEW -> {
                binding.setThemeButton.apply {
                    text = resources.getString(R.string.button_label_download_theme)
                    enabledButtonTheme?.backgroundColor?.let { setBackgroundColor(it) }
                }
            }
            DetailsViewModel.ThemeState.DOWNLOADED -> {
                binding.setThemeButton.apply {
                    text = resources.getString(R.string.button_label_set_theme)
                    enabledButtonTheme?.backgroundColor?.let { setBackgroundColor(it) }
                }

            }
            DetailsViewModel.ThemeState.ACTIVE -> {
                binding.setThemeButton.apply {
                    text = resources.getString(R.string.button_label_theme_is_active)
                    isClickable = false
                    disabledButtonTheme?.let { buttonTheme ->
                        buttonTheme.backgroundColor?.let { setBackgroundColor(it) }
                        buttonTheme.textColor?.let { setTextColor(it) }
                        buttonTheme.iconTintResource?.let { setIconTintResource(it) }
                        icon = buttonTheme.icon
                    }
                }
            }
        }
    }

    private fun callLoadingDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater

        builder.setView(inflater.inflate(R.layout.dialog_loading, null))
            .setCancelable(false)
        val dialog = builder.create()
        val background = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(background, 32)
        dialog?.window?.setBackgroundDrawable(inset)
        dialog.show()
        return dialog
    }

    private fun setTheme() {
        val themeState = detailsViewModel.checkThemeState(detailsFragmentArgs.themeId)

        val loadingDialog = callLoadingDialog()

        if (themeState == DetailsViewModel.ThemeState.NEW) {
            requestThemeDownload()
        }
        //TODO: Set theme
        //TODO: .onCompletion { loadingDialog.dismiss() }
    }

    private fun requestThemeDownload() {
        detailsViewModel.downloadTheme(detailsFragmentArgs.themeId)
    }

    private fun callMessageDialog(type: BaseDialogFragment.Companion.DialogType) {
        val dialog = BaseDialogFragment()
        val args = Bundle()
        args.putSerializable(DIALOG_TYPE, type)
        dialog.arguments = args
        dialog.show(requireActivity().supportFragmentManager, TAG)
    }

    private fun callSuccessMessage() {
       binding.successMessage.visibility = View.VISIBLE
    }
}