package com.wallpaperscraft.keby.app.ui.settings.insides.language

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.models.LanguageOption
import com.wallpaperscraft.keby.databinding.ItemSettingsLanguageOptionBinding

class LanguageOptionAdapter :
    ListAdapter<LanguageOption, LanguageOptionAdapter.LanguageOptionViewHolder>(
        DiffCallback
    ) {

    private var onDownloadListener: ((String) -> Unit)? = null

    fun setOnDownloadListener(listener: (String) -> Unit) {
        onDownloadListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageOptionViewHolder =
        LanguageOptionViewHolder(
            ItemSettingsLanguageOptionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: LanguageOptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LanguageOptionViewHolder(
        private val binding: ItemSettingsLanguageOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: LanguageOption) {
            binding.languageLabel.text = item.language
            binding.buttonLanguageAction.setOnClickListener {
                onDownloadButtonClick(item)
            }
        }

        private fun onDownloadButtonClick(item: LanguageOption) {
            onDownloadListener?.invoke(item.url)

            binding.buttonLanguageAction.apply {
                setImageResource(R.drawable.ic_close)
                imageTintList = ContextCompat.getColorStateList(
                    context, R.color.button_remove_language_color
                )
                setOnClickListener {
                    onCancelDownloadClick(item)
                }
            }

            binding.downloadProgressBar.visibility = View.VISIBLE
        }

        private fun onCancelDownloadClick(item: LanguageOption) {
            binding.downloadProgressBar.visibility = View.GONE
            binding.buttonLanguageAction.apply {
                setImageResource(R.drawable.ic_download)
                imageTintList =
                    ContextCompat.getColorStateList(context, R.color.button_download_color)
                setOnClickListener {
                    onDownloadButtonClick(item)
                }
            }
            //TODO: Отмена загрузки
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LanguageOption>() {
            override fun areItemsTheSame(
                oldItem: LanguageOption,
                newItem: LanguageOption
            ): Boolean {
                return oldItem.language == newItem.language
            }

            override fun areContentsTheSame(
                oldItem: LanguageOption,
                newItem: LanguageOption
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}