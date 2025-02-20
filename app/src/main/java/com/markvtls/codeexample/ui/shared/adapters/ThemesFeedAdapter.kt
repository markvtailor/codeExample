package com.wallpaperscraft.keby.app.ui.shared.adapters

import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wallpaperscraft.keby.app.ui.models.Theme
import com.wallpaperscraft.keby.app.utils.getDeviceScreenHeight
import com.wallpaperscraft.keby.app.utils.getDefaultDrawable
import com.wallpaperscraft.keby.databinding.ItemKeyboardThemeBinding

class ThemesFeedAdapter(
    private val onItemClicked: (Theme) -> Unit,
    private val context: Context
) : ListAdapter<Theme, ThemesFeedAdapter.ThemesViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemesViewHolder =
        ThemesViewHolder(
            ItemKeyboardThemeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            context = context
        )

    override fun onBindViewHolder(holder: ThemesViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onItemClicked(getItem(position))
        }
    }

    class ThemesViewHolder(
        private val binding: ItemKeyboardThemeBinding, private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Theme) {
            binding.itemImage.layoutParams.height = (context.getDeviceScreenHeight() / 6.5).toInt()
            binding.itemImage.setImageDrawable(getDefaultDrawable())
        }
    }

    companion object {

        private val DiffCallback = object : DiffUtil.ItemCallback<Theme>() {
            override fun areItemsTheSame(oldItem: Theme, newItem: Theme): Boolean {
                return oldItem.themeId == newItem.themeId
            }

            override fun areContentsTheSame(oldItem: Theme, newItem: Theme): Boolean {
                return oldItem == newItem
            }


        }

    }
}