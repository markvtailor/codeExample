package com.wallpaperscraft.keby.app.ui.adapters

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wallpaperscraft.keby.R

class SnapPaginator(
    private val recyclerView: RecyclerView,
    private val loadMore: (() -> Unit)?,
    private val noMoreItems: (() -> Boolean),
    private val loadingTriggerThreshold: Int = 1,
    loadingResourceId: Int,
    errorResourceId: Int,
    errorResourceRetryButton: Int,
    errorResourceTextView: Int,
    repeatListener: (() -> Unit)?,
    errorBind: ((itemView: View) -> Unit)? = null,
    loadingBind: ((itemView: View) -> Unit)? = null,
    errorUnbind: ((itemView: View) -> Unit)? = null,
    loadingUnbind: ((itemView: View) -> Unit)? = null
) {

    private val paginatorAdapter: SnapPaginatorAdapter
    private val userAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = recyclerView.adapter
    private var paginateSnapSizeLookup: GridLayoutManager.SpanSizeLookup? = null
    private var paginateAdapterDataObserver: SnapPaginateDataObserver

    private var isError: Boolean = false
    private var isLoading: Boolean = false


    private val isCanLoadMore: Boolean
        get() = !isLoading && !isError && !noMoreItems.invoke()

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            checkScroll()
        }
    }

    init {
        paginatorAdapter = SnapPaginatorAdapter(
            loadingResourceId = loadingResourceId ,
            errorResourceId = errorResourceId,
            errorResourceRetryButton = errorResourceRetryButton,
            errorResourceTextView = errorResourceTextView,
            userAdapter = userAdapter!!,
            repeatListener = repeatListener,
            errorBind = errorBind,
            loadingBind = loadingBind,
            errorUnbind = errorUnbind,
            loadingUnbind = loadingUnbind
        )

        paginateAdapterDataObserver = SnapPaginateDataObserver(paginatorAdapter) {
            recyclerView.post {
                paginatorAdapter.stateChanged(if (noMoreItems.invoke()) NO_MORE_ITEMS else if (isError) ERROR else LOADING)
                checkScroll()
            }
        }
        recyclerView.setOnScrollListener(scrollListener)
        recyclerView.adapter = paginatorAdapter
        userAdapter.registerAdapterDataObserver(paginateAdapterDataObserver)
    }


    private fun checkScroll() {
        if (isOnBottom(recyclerView)) {
            if(isCanLoadMore) {
                loadMore?.invoke()
            }
        }
    }

    private fun isOnBottom(recyclerView: RecyclerView?): Boolean {
        if (recyclerView != null) {
            val totalCount = if (recyclerView.layoutManager != null) recyclerView.layoutManager!!.itemCount else 0
            val lastVisiblePosition = getLastVisibleItemPositionByLayoutManager(recyclerView.layoutManager)
            val result = (totalCount <= lastVisiblePosition + loadingTriggerThreshold) || totalCount == 0
            return result
        } else return false
    }

    //Нужно ли проверять тип у нас?
    private fun getLastVisibleItemPositionByLayoutManager(layoutManager: RecyclerView.LayoutManager?): Int = if (layoutManager != null) {
        if (layoutManager is GridLayoutManager) {
            layoutManager.findLastVisibleItemPosition()
        } else 0
    } else {
        0
    }

    private fun setNoMoreItems(noMoreItems: Boolean) {
        if (noMoreItems) {
            paginatorAdapter.stateChanged(NO_MORE_ITEMS)
        }
    }

    fun showError(isShowError: Boolean, messageResId: Int = R.string.error_retry_message) {
        if (isShowError) {
            isError = true
            paginatorAdapter.stateChanged(ERROR)
            paginatorAdapter.setErrorMessage(messageResId)
        } else {
            isError = false
            setNoMoreItems(noMoreItems.invoke())
        }
    }

    fun showLoading(isShowLoading: Boolean, isLoading: Boolean = false) {
        if (isShowLoading) {
            paginatorAdapter.stateChanged(LOADING)
        }
        this.isLoading = isLoading
    }

    fun unbind() {
        recyclerView.removeOnScrollListener(scrollListener)

        ///???
//        if (recyclerView.layoutManager is GridLayoutManager && paginateSnapSizeLookup != null) {
//            val spanSizeLookup = object : SpanSizeLookup() {
//                override fun getSpanSize(p0: Int): Int {
//                    return 1
//                }
//
//            }
//            (recyclerView.layoutManager as GridLayoutManager).spanSizeLookup = spanSizeLookup
//        }
    }


    companion object {
        const val NO_MORE_ITEMS = 0
        const val LOADING = 1
        const val ERROR = 2
    }
}