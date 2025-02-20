package com.wallpaperscraft.keby.app.ui.settings.insides.language

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.shared.adapters.ItemDeleteListener
import com.wallpaperscraft.keby.databinding.ItemSettingsDownloadedLanguageBinding
import java.util.Collections

class DownloadedLanguageAdapter : ListAdapter<String, DownloadedLanguageAdapter.DragViewHolder>(
    DiffCallback
), ItemTouchHelperAdapter {

    private var languagesArray: ArrayList<String> = ArrayList()

    private var removalDialogListener: ((onApprove: () -> Unit, onDismiss: () -> Unit) -> Unit)? =
        null
    private var onRemoveListener: ((Int) -> Unit)? = null

    //TODO: Переписать OnDownload под настоящую имплементацию
    private var onDownloadListener: ((String) -> Unit)? = null

    fun setData(languagesArray: ArrayList<String>) {
        languagesArray.toCollection(this.languagesArray)
        notifyDataSetChanged()
    }

    fun setRemovalDialogListener(listener: (onApprove: () -> Unit, onDismiss: () -> Unit) -> Unit) {
        removalDialogListener = listener
    }

    fun setOnRemovalListener(listener: (Int) -> Unit) {
        onRemoveListener = listener
    }

    fun setOnDownloadListener(listener: (String) -> Unit) {
        onDownloadListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DragViewHolder {
        val binding = ItemSettingsDownloadedLanguageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DragViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DragViewHolder, position: Int) {

        val deleteListener = object : ItemDeleteListener {
            override fun delete(position: Int) {
                languagesArray.removeAt(position)
                onRemoveListener?.invoke(position)
                notifyItemRemoved(position)
                if (itemCount == 1) notifyDataSetChanged() //TODO: Анимации?
            }
        }

        languagesArray[position].let { item ->
            holder.setData(item)
            holder.bindDeleteButton(listener = deleteListener)
            holder.binding.buttonLanguageAction.isVisible = itemCount != 1
        }
    }

    override fun getItemCount(): Int {
        return languagesArray.size
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(languagesArray, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        //TODO("Not yet implemented")
    }

    inner class DragViewHolder(
        val binding: ItemSettingsDownloadedLanguageBinding
    ) : ViewHolder(binding.root), ItemTouchHelperViewHolder {

        //TODO: Рефакторинг???
        fun bindDeleteButton(listener: ItemDeleteListener) {
            binding.buttonLanguageAction.setOnClickListener {
                onRemovalDialogCall()
                removalDialogListener?.let { dialogCall ->
                    dialogCall(
                        //OnApprove
                        {
                            listener.delete(adapterPosition)
                            onRemovalDialogDismiss()
                        },
                        //OnDismiss
                        {
                            onRemovalDialogDismiss()
                        }
                    )
                }
            }
        }

        private fun onRemovalDialogDismiss() {
            binding.buttonLanguageAction.apply {
                imageTintList = ColorStateList.valueOf(context.getColor(R.color.light_text))
            }
            binding.languageLabel.apply {
                setTextColor(context.getColor(R.color.light_text))
            }
        }

        private fun onRemovalDialogCall() {
            binding.buttonLanguageAction.apply {
                imageTintList = ColorStateList.valueOf(context.getColor(R.color.orange))
            }
            binding.languageLabel.apply {
                setTextColor(context.getColor(R.color.orange))
            }
        }

        fun setData(data: String) {
            binding.languageLabel.text = data
        }

        override fun onItemDragged() {
            binding.root.background = ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.option_dragged))
        }

        override fun onItemPlaced() {
            binding.root.background = ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.black_2))
        }

    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}

interface ItemTouchHelperViewHolder{

    fun onItemDragged()

    fun onItemPlaced()

}

interface ItemTouchHelperAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDismiss(position: Int)

}

class ReorderHelperCallback(private val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        val dragFlags =
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder
    ): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
        when(actionState) {
            ACTION_STATE_DRAG -> {
                (viewHolder as ItemTouchHelperViewHolder).onItemDragged()
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
        (viewHolder as ItemTouchHelperViewHolder).onItemPlaced()
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        //TODO("Not yet implemented")
    }

}