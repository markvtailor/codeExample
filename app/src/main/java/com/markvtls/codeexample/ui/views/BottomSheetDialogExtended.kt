package com.wallpaperscraft.keby.app.ui.views

import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.shared.fragments.BaseDialogFragment
import com.wallpaperscraft.keby.app.ui.shared.fragments.BaseDialogFragment.Companion.DIALOG_TYPE
import com.wallpaperscraft.keby.app.ui.shared.themes.ButtonStateTheme
import com.wallpaperscraft.keby.databinding.BottomDialogKeyboardActivationBinding

class BottomSheetDialogExtended :
    BottomSheetDialogFragment(R.layout.bottom_dialog_keyboard_activation) {

    private var _binding: BottomDialogKeyboardActivationBinding? = null
    private val binding get() = _binding!!

    //TODO: При необходимости можно сделать этим значениям get/set и собрать любую нужную тему
    private var enabledButtonTheme: ButtonStateTheme? = null
    private var disabledButtonTheme: ButtonStateTheme? = null

    private var stateListener: (() -> Unit)? = null

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomDialogKeyboardActivationBinding.inflate(inflater, container, false)
        val filter = IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED)
        registerReceiver(
            requireContext(),
            InputMethodChangeReceiver(this),
            filter,
            RECEIVER_EXPORTED
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout

            BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                isDraggable = false
                isCancelable = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enabledButtonTheme = ButtonStateTheme.Builder(requireContext()).buildEnabledButtonTheme()
        disabledButtonTheme = ButtonStateTheme.Builder(requireContext()).buildDisableButtonTheme()

        val type = arguments?.getSerializable(WELCOME_DIALOG_TYPE) as DialogType?

        if (type != null) {
            when (type) {
                DialogType.WELCOME -> {
                    binding.buttonChooseKeyboard.isEnabled = false
                    activateButton(binding.buttonActivateKeyboard)
                }

                DialogType.RESET_LIST -> {
                    binding.title.text = resources.getString(R.string.title_reset)
                    binding.buttonChooseKeyboard.isEnabled = false
                    activateButton(binding.buttonActivateKeyboard)
                }
                DialogType.RESET_KEYBOARD -> {
                    activateButton(binding.buttonChooseKeyboard)
                    deactivateActivateKeyboardButton()
                    binding.title.text = resources.getString(R.string.title_reset)
                }
            }
        }

        binding.buttonActivateKeyboard.setOnClickListener {
            callMessageDialog()
        }

        binding.buttonChooseKeyboard.setOnClickListener {
            openInputMethodDialog()
        }


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        stateListener?.invoke()
    }

    private fun openSettingsInputMethodPage() {

        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)

        resultLauncher.launch(intent)
    }

    private fun openInputMethodDialog() {
        val im = getSystemService(requireContext(), InputMethodManager::class.java)
        im!!.showInputMethodPicker()
    }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->

            if (checkIfKebyIsInList()) {
                deactivateActivateKeyboardButton()
                activateButton(binding.buttonChooseKeyboard)
            }

        }

    private fun checkIfKebyIsInList(): Boolean {
        val inputManager = getSystemService(requireContext(), InputMethodManager::class.java)
        val isKebySelected =
            inputManager!!.enabledInputMethodList.find { it.packageName == PACKAGE_NAME } != null
        return isKebySelected
    }

    private fun deactivateActivateKeyboardButton() {
        binding.buttonActivateKeyboard.apply {
            isEnabled = false
            disabledButtonTheme?.let { buttonTheme ->
                buttonTheme.backgroundColor?.let { setBackgroundColor(it) }
                buttonTheme.textColor?.let { setTextColor(it) }
                buttonTheme.iconTintResource?.let { setIconTintResource(it) }
                icon = buttonTheme.icon
            }
            text = resources.getString(R.string.button_activate_keyboard_label_done)
        }
    }

    private fun activateButton(button: MaterialButton) {
        button.apply {
            isEnabled = true
            enabledButtonTheme?.let { buttonTheme ->
                buttonTheme.backgroundColor?.let { setBackgroundColor(it) }
                buttonTheme.textColor?.let { setTextColor(it) }
            }
        }
    }

    private fun callMessageDialog() {
        val dialog = BaseDialogFragment()
        val args = Bundle()
        args.putSerializable(DIALOG_TYPE, BaseDialogFragment.Companion.DialogType.PRIVACY_MESSAGE)
        dialog.apply {
            arguments = args
            setDismissButtonListener { openSettingsInputMethodPage() }
        }
        dialog.show(requireActivity().supportFragmentManager, TAG)
    }

    fun setDialogStateListener(listener: () -> Unit) {
        stateListener = listener
    }

    private class InputMethodChangeReceiver(private val dialogExtended: BottomSheetDialogExtended) :
        BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val currentInputMethod = Settings.Secure.getString(
                context?.contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            )
            if (currentInputMethod == FULL_NAME) {
                dialogExtended.dismiss()
            }
        }
    }

    companion object {
        const val PACKAGE_NAME = "com.wallpaperscraft.keby"
        const val FULL_NAME = "com.wallpaperscraft.keby/.app.services.KeyboardService"
        const val TAG = "PRIVACY_DIALOG"
        const val WELCOME_DIALOG_TYPE = "welcome_dialog_type"

        enum class DialogType {
            WELCOME,
            RESET_KEYBOARD,
            RESET_LIST
        }

        enum class DialogState {
            EXPANDED,
            HIDDEN
        }
    }


}