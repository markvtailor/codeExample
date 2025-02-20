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
import com.wallpaperscraft.keby.databinding.DialogActionMessageBinding

class ActionDialogFragment : DialogFragment() {

    private var _binding: DialogActionMessageBinding? = null
    private val binding get() = _binding!!

    private var dialogActionListener: (() -> Unit)? = null
    private var dismissActionListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogActionMessageBinding.inflate(LayoutInflater.from(context))
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)

            binding.dismissButton.setOnClickListener {
                dismissActionListener?.invoke()
                dismiss()
            }

            val type = arguments?.getSerializable(DIALOG_TYPE) as ActionType?

            if (type != null) {
                when (type) {
                    ActionType.REMOVAL -> {
                        with(binding) {
                            title.visibility = View.GONE
                            messageText.text =
                                resources.getString(R.string.message_language_removal)
                            actionButton.setOnClickListener { dialogActionListener?.invoke() }
                        }
                    }
                }
            }

            val dialog = builder.create()
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    fun setOnDialogActionListener(action: () -> Unit) {
        dialogActionListener = action
    }

    fun setOnDialogDismissListener(onDismissAction: () -> Unit) {
        dismissActionListener = onDismissAction
    }

    companion object {
        enum class ActionType {
            REMOVAL
        }

        const val DIALOG_TYPE = "dialog_type"
        const val TAG = "action_dialog"
    }
}