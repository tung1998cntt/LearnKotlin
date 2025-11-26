package com.example.learnkotlin.presentation.base.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListUpdateCallback

class BaseCompositeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val adapters = mutableListOf<BaseItemAdapter<out RecyclerView.ViewHolder, *>>()
    private val startPositions = mutableListOf<Int>()



    // LoadMore
    var onLoadMore: (() -> Unit)? = null
    var isLoadingMore = false
    var isLastPage = false
    var loadMoreThreshold = 5


    fun attachToRecyclerView(rv: RecyclerView, layoutManager: LinearLayoutManager) {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0 || isLoadingMore || isLastPage) return
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (totalItemCount <= lastVisible + loadMoreThreshold) {
                    isLoadingMore = true
                    onLoadMore?.invoke()
                }
            }
        })
    }

    fun loadMoreFinished() {
        isLoadingMore = false
    }

    fun setLastPage(lastPage: Boolean) {
        isLastPage = lastPage
    }

    // ===================== POSITION HELPERS ==========================
    private fun recalcStartPositions() {
        startPositions.clear()
        var current = 0
        adapters.forEach { ad ->
            startPositions.add(current)
            current += ad.getItemCount()
        }
    }

    private fun findAdapterByPosition(globalPos: Int):
            Pair<BaseItemAdapter<out RecyclerView.ViewHolder, *>, Int>? {
        for (i in adapters.indices) {
            val start = startPositions[i]
            val end = start + adapters[i].getItemCount()
            if (globalPos in start until end) {
                return adapters[i] to (globalPos - start)
            }
        }
        return null
    }

    internal fun getAdapterStartPosition(adapter: BaseItemAdapter<*, *>): Int {
        val index = adapters.indexOf(adapter)
        return if (index >= 0) startPositions[index] else -1
    }

    // ===================== ADD / REMOVE ADAPTER ==========================
    fun addAdapter(adapter: BaseItemAdapter<out RecyclerView.ViewHolder, *>) {
        adapter.attach(this)
        val insertStart = itemCount
        adapters.add(adapter)
        recalcStartPositions()
        notifyItemRangeInserted(insertStart, adapter.getItemCount())
    }

    fun addAdapterAt(index: Int, adapter: BaseItemAdapter<out RecyclerView.ViewHolder, *>) {
        val insertStart = if (index == adapters.size) itemCount else startPositions[index]
        adapter.attach(this)
        adapters.add(index, adapter)
        recalcStartPositions()
        notifyItemRangeInserted(insertStart, adapter.getItemCount())
    }

    fun removeAdapter(adapter: BaseItemAdapter<out RecyclerView.ViewHolder, *>) {
        val start = getAdapterStartPosition(adapter)
        if (start == -1) return
        val count = adapter.getItemCount()
        adapters.remove(adapter)
        recalcStartPositions()
        notifyItemRangeRemoved(start, count)
    }

    // ===================== CHILD ADAPTER NOTIFICATIONS ==================
    internal fun notifyItemInsertedFromChild(child: BaseItemAdapter<*, *>, localPos: Int) {
        val global = getAdapterStartPosition(child) + localPos
        notifyItemInserted(global)
    }

    internal fun notifyItemRemovedFromChild(child: BaseItemAdapter<*, *>, localPos: Int) {
        val global = getAdapterStartPosition(child) + localPos
        notifyItemRemoved(global)
    }

    internal fun notifyItemChangedFromChild(child: BaseItemAdapter<*, *>, localPos: Int) {
        val global = getAdapterStartPosition(child) + localPos
        notifyItemChanged(global)
    }

    internal fun notifyRangeInsertedFromChild(child: BaseItemAdapter<*, *>, localPos: Int, count: Int) {
        val global = getAdapterStartPosition(child) + localPos
        notifyItemRangeInserted(global, count)
    }

    internal fun notifyItemRangeChangedFromChild(
        child: BaseItemAdapter<*, *>,
        localStart: Int,
        count: Int,
        payload: Any? = null
    ) {
        val global = getAdapterStartPosition(child) + localStart
        notifyItemRangeChanged(global, count, payload)
    }

    internal fun onChildDataChanged(child: BaseItemAdapter<*, *>, diff: DiffUtil.DiffResult) {
        val start = getAdapterStartPosition(child)
        val callback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(start + position, count)
            }
            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(start + position, count)
            }
            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(start + fromPosition, start + toPosition)
            }
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyItemRangeChanged(start + position, count, payload)
            }
        }
        recalcStartPositions() // update startPositions trước khi dispatch
        diff.dispatchUpdatesTo(callback)
    }

    // ===================== OVERRIDES ==========================
    override fun getItemCount(): Int = adapters.sumOf { it.getItemCount() }

    override fun getItemViewType(position: Int): Int {
        val (adapter, local) = findAdapterByPosition(position)
            ?: error("Invalid global position")
        return adapter.getItemViewType(local)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        adapters.forEach { ad ->
            val vh = ad.tryCreate(parent, viewType)
            if (vh != null) return vh
        }
        error("No adapter can handle viewType=$viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (adapter, local) = findAdapterByPosition(position)
            ?: error("Invalid global position")
        @Suppress("UNCHECKED_CAST")
        (adapter as BaseItemAdapter<RecyclerView.ViewHolder, *>)
            .onBindVH(holder, local)
    }

    // ===================== Optional: SpanSizeLookup ====================
    fun createSpanSizeLookup(defaultSpan: Int = 1): androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup {
        return object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val (adapter, local) = findAdapterByPosition(position)
                    ?: return defaultSpan
                return if (adapter is SpanSizeProvider) adapter.getSpanSize(local) else defaultSpan
            }
        }
    }

    interface SpanSizeProvider {
        fun getSpanSize(position: Int): Int
    }
}

