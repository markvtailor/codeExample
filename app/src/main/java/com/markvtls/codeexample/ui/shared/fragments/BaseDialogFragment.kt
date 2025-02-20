package com.wallpaperscraft.keby.app.ui.shared.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.databinding.DialogRemovalMessageBinding

class BaseDialogFragment : DialogFragment() {


    private var _binding: DialogRemovalMessageBinding? = null
    private val binding get() = _binding!!

    private var dismissButtonListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRemovalMessageBinding.inflate(LayoutInflater.from(context))
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)

            binding.dismissButton.setOnClickListener {
                dismissButtonListener?.invoke()
                dismiss()
            }

            val type = arguments?.getSerializable(DIALOG_TYPE) as DialogType?

            if (type != null) {

                when (type) {
                    DialogType.REMOVAL_MESSAGE -> {
                        binding.apply {
                            messageText.text = resources.getString(R.string.removal_dialog_message)
                            errorImage.visibility = View.GONE
                            title.visibility = View.GONE
                        }
                    }

                    DialogType.ERROR_MESSAGE_THEME_CONNECTION -> {
                        binding.apply {
                            title.text = resources.getString(R.string.error_dialog_title)
                            messageText.text =
                                resources.getString(R.string.connection_error_dialog_message_theme)
                        }
                    }

                    DialogType.ERROR_MESSAGE_THEME_SERVER -> {
                        binding.apply {
                            title.text = resources.getString(R.string.error_dialog_title)
                            messageText.text =
                                resources.getString(R.string.server_error_dialog_message_theme)
                        }
                    }

                    DialogType.PRIVACY_MESSAGE -> {
                        binding.apply {
                            title.text = resources.getString(R.string.title_privacy)
                            messageText.text = resources.getString(R.string.message_privacy)
                            errorImage.setImageResource(R.drawable.ic_keby_message)
                        }
                    }
                }
            }


            val dialog = builder.create()
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setDismissButtonListener(listener: () -> Unit) {
        dismissButtonListener = listener
    }


    override fun getTheme(): Int = R.style.RoundedCornersDialog


    companion object {
        enum class DialogType {
            REMOVAL_MESSAGE,
            ERROR_MESSAGE_THEME_CONNECTION,
            ERROR_MESSAGE_THEME_SERVER,
            PRIVACY_MESSAGE
        }

        const val DIALOG_TYPE = "dialog_type"
        const val TAG = "message_dialog"
    }
}