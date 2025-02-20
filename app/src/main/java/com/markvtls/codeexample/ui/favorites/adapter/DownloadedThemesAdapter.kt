package com.wallpaperscraft.keby.app.ui.favorites.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wallpaperscraft.keby.app.ui.models.DownloadedTheme
import com.wallpaperscraft.keby.app.ui.shared.adapters.ItemDeleteListener
import com.wallpaperscraft.keby.app.utils.getDeviceScreenHeight
import com.wallpaperscraft.keby.app.utils.getDefaultDrawable
import com.wallpaperscraft.keby.databinding.ItemDownloadedKeyboardThemeBinding

class DownloadedThemesAdapter(
    private val onItemClicked: (DownloadedTheme) -> Unit,
    private val onRemoveItemButtonClicked: (Int) -> Unit,
    private val context: Context
) : ListAdapter<DownloadedTheme, DownloadedThemesAdapter.DownloadedThemesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedThemesViewHolder =
        DownloadedThemesViewHolder(
            ItemDownloadedKeyboardThemeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), context = context
        )

    override fun onBindViewHolder(holder: DownloadedThemesViewHolder, position: Int) {

        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onItemClicked(getItem(position))
        }
    }

    override fun onBindViewHolder(
        holder: DownloadedThemesViewHolder, position: Int, payloads: MutableList<Any>
    ) {

        val listener = object : ItemDeleteListener {
            override fun delete(position: Int) {
                val newItems = currentList.toMutableList()
                newItems.removeAt(position)
                submitList(newItems)
                onRemoveItemButtonClicked(getItem(position).themeId)
            }

        }

        when (val latestPayload = payloads.lastOrNull()) {
            is ThemeChangePayload.ActiveTheme -> holder.bindActiveOverlay(latestPayload.isActive)
            is ThemeChangePayload.DeletionMark -> holder.bindDeleteButton(
                latestPayload.isMarkedForRemoval, listener
            )

            else -> {
                onBindViewHolder(holder, position)
            }
        }
    }


    private sealed interface ThemeChangePayload {

        data class ActiveTheme(val isActive: Boolean) : ThemeChangePayload

        data class DeletionMark(val isMarkedForRemoval: Boolean) : ThemeChangePayload
    }

    class DownloadedThemesViewHolder(
        private val binding: ItemDownloadedKeyboardThemeBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: DownloadedTheme) {

            binding.itemImage.layoutParams.height = (context.getDeviceScreenHeight() / 6.5).toInt()


            bindActiveOverlay(item.isActive)
            bindDeleteButton(item.markedForDeletion, null)
            binding.itemImage.setImageDrawable(getDefaultDrawable())
        }

        fun bindActiveOverlay(isActive: Boolean) {
            if (isActive) {
                binding.activeThemeOverlay.visibility = View.VISIBLE
            } else binding.activeThemeOverlay.visibility = View.GONE
        }

        fun bindDeleteButton(isMarkedForRemoval: Boolean, listener: ItemDeleteListener?) {
            if (binding.activeThemeOverlay.visibility == View.GONE && isMarkedForRemoval) {
                binding.buttonRemove.visibility = View.VISIBLE
                binding.buttonRemove.setOnClickListener {
                    listener?.delete(adapterPosition)
                }
            } else binding.buttonRemove.visibility = View.GONE
        }
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DownloadedTheme>() {
            override fun areItemsTheSame(
                oldItem: DownloadedTheme, newItem: DownloadedTheme
            ): Boolean {
                return oldItem.themeId == newItem.themeId
            }

            override fun areContentsTheSame(
                oldItem: DownloadedTheme, newItem: DownloadedTheme
            ): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(
                oldItem: DownloadedTheme, newItem: DownloadedTheme
            ): Any? {

                return when {
                    oldItem.markedForDeletion != newItem.markedForDeletion -> {
                        ThemeChangePayload.DeletionMark(newItem.markedForDeletion)
                    }

                    oldItem.isActive != newItem.isActive -> {
                        ThemeChangePayload.ActiveTheme(newItem.isActive)
                    }

                    else -> super.getChangePayload(oldItem, newItem)
                }

            }
        }
    }

}