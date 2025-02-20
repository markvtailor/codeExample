package com.wallpaperscraft.keby.app.ui.adapters

import androidx.recyclerview.widget.RecyclerView

class SnapPaginateDataObserver(
    private val paginateAdapter: SnapPaginatorAdapter,
    private val onAdapterChange: () -> Unit
): RecyclerView.AdapterDataObserver() {

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        paginateAdapter.notifyItemRangeChanged(positionStart, itemCount)
        onAdapterChange.invoke()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        paginateAdapter.notifyItemRangeChanged(positionStart, itemCount, payload)
        onAdapterChange.invoke()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        paginateAdapter.notifyItemRangeChanged(positionStart, itemCount)
        onAdapterChange.invoke()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        paginateAdapter.notifyItemRangeChanged(positionStart, itemCount)
        onAdapterChange.invoke()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        paginateAdapter.notifyItemRangeChanged(fromPosition, toPosition)
        onAdapterChange.invoke()
    }

    override fun onChanged() {
        paginateAdapter.notifyDataSetChanged()
        onAdapterChange.invoke()
    }



}