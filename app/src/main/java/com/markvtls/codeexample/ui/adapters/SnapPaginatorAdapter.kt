package com.wallpaperscraft.keby.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.adapters.SnapPaginator.Companion.ERROR
import com.wallpaperscraft.keby.app.ui.adapters.SnapPaginator.Companion.LOADING

class SnapPaginatorAdapter internal constructor(
    private val loadingResourceId: Int,
    private val errorResourceId: Int,
    private val errorResourceTextView: Int,
    private val errorResourceRetryButton: Int,
    private val userAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    private var repeatListener: (() -> Unit)?,
    private val errorBind: ((itemView: View) -> Unit)? = null,
    private val loadingBind: ((itemView: View) -> Unit)? = null,
    private val errorUnbind: ((itemView: View) -> Unit)? = null,
    private val loadingUnbind: ((itemView: View) -> Unit)? = null,
    private val errorAttached: ((holder: RecyclerView.ViewHolder) -> Unit)? = null,
    private val loadingAttached: ((holder: RecyclerView.ViewHolder) -> Unit)? = null,
    private val errorDetached: ((holder: RecyclerView.ViewHolder) -> Unit)? = null,
    private val loadingDetached: ((holder: RecyclerView.ViewHolder) -> Unit)? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var paginateStatus = NO_STATUS

    private val errorOrLoadingItemPosition: Int
        get() = if (isErrorOrLoading) itemCount - 1 else -1

    private val isErrorOrLoading: Boolean
        get() = paginateStatus == LOADING || paginateStatus == ERROR

    private var errorMessage: Int = R.string.error_retry_message


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType) {
            ITEM_VIEW_TYPE_LOADING -> object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(loadingResourceId, parent, false)) {}
            ITEM_VIEW_TYPE_ERROR -> object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(errorResourceId, parent, false)) {}
            else -> userAdapter.onCreateViewHolder(parent, viewType)
        }


    override fun getItemCount(): Int = if (isErrorOrLoading) userAdapter.itemCount + 1 else userAdapter.itemCount

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            isErrorItem(position) -> {
                errorBind?.invoke(holder.itemView)
                holder.itemView.findViewById<AppCompatTextView>(errorResourceTextView).text = holder.itemView.resources.getText(errorMessage)
                holder.itemView.findViewById<View>(errorResourceRetryButton)?.setOnClickListener {
                    repeatListener?.invoke()
                }
                val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                layoutParams.isFullSpan = true
            }
            isLoadingItem(position) -> {
                loadingBind?.invoke(holder.itemView)
            }
            position == RecyclerView.NO_POSITION -> {}
            else -> userAdapter.onBindViewHolder(holder, position)
        }
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
        userAdapter.setHasStableIds(hasStableIds)
    }

    override fun getItemViewType(position: Int): Int = when {
        isErrorItem(position) -> ITEM_VIEW_TYPE_ERROR
        isLoadingItem(position) -> ITEM_VIEW_TYPE_LOADING
        else -> userAdapter.getItemViewType(position)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when {
            isErrorItem(holder.adapterPosition) -> {
                errorUnbind?.invoke(holder.itemView)
            }
            isLoadingItem(holder.adapterPosition) -> {
                loadingUnbind?.invoke(holder.itemView)
            }
            holder.adapterPosition == RecyclerView.NO_POSITION -> {}
            else -> {
                if (holder.itemViewType != ITEM_VIEW_TYPE_LOADING
                    || holder.itemViewType != ITEM_VIEW_TYPE_ERROR) userAdapter.onViewRecycled(holder)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)

        when {
            isErrorItem(holder.adapterPosition) -> {
                errorAttached?.invoke(holder)
            }
            isLoadingItem(holder.adapterPosition) -> {
                loadingAttached?.invoke(holder)
            }
            holder.adapterPosition == RecyclerView.NO_POSITION -> {}
            else -> userAdapter.onViewAttachedToWindow(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        when {
            isErrorItem(holder.adapterPosition) -> {
                errorDetached?.invoke(holder)
            }
            isLoadingItem(holder.adapterPosition) -> {
                loadingDetached?.invoke(holder)
            }
            holder.adapterPosition == RecyclerView.NO_POSITION -> {}
            else -> userAdapter.onViewDetachedFromWindow(holder)
        }
    }

    private fun isErrorItem(position: Int) = paginateStatus == ERROR && position == errorOrLoadingItemPosition

    private fun isLoadingItem(position: Int) = paginateStatus == LOADING && position == errorOrLoadingItemPosition

    fun setErrorMessage(messageId: Int) {
        errorMessage = messageId
    }

    internal fun stateChanged(status: Int) {
        if (this.paginateStatus != status) {
            this.paginateStatus = status
            if (errorOrLoadingItemPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(errorOrLoadingItemPosition)
            }
        }
    }

//    internal fun unbind() {
//        repeatListener = null
//    }

    companion object {
        const val ITEM_VIEW_TYPE_LOADING = 46699933
        const val ITEM_VIEW_TYPE_ERROR = 46699932
        const val NO_STATUS = -1
    }


}