package com.example.learnkotlin.presentation.base.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseItemAdapter<VH : RecyclerView.ViewHolder, T> {

    private var parent: BaseCompositeAdapter? = null
    internal fun attach(parent: BaseCompositeAdapter) {
        this.parent = parent
    }

    protected val items = mutableListOf<T>()

    // ========================= Data Handling ===========================
    /** Submit new list with DiffUtil */
    fun submitList(newItems: List<T>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                this@BaseItemAdapter.areItemsTheSame(
                    items[oldItemPosition],
                    newItems[newItemPosition]
                )

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                this@BaseItemAdapter.areContentsTheSame(
                    items[oldItemPosition],
                    newItems[newItemPosition]
                )
        })

        items.clear()
        items.addAll(newItems)
        parent?.onChildDataChanged(this, diff)
    }

    /** Refresh all items using DiffUtil (full update) */
    fun refreshAll() {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = items.size
            override fun areItemsTheSame(old: Int, new: Int) = false
            override fun areContentsTheSame(old: Int, new: Int) = false
        })
        parent?.onChildDataChanged(this, diff)
    }

    /** Notify multiple items changed (rời rạc) */
    fun notifyItemsChanged(vararg localPositions: Int) {
        localPositions.forEach { pos ->
            parent?.notifyItemChangedFromChild(this, pos)
        }
    }

    open fun getItemCount() = items.size
    fun getItem(position: Int) = items[position]

    /** CALLBACK LOCAL → GLOBAL POSITION */
    fun notifyItemInserted(localPos: Int) = parent?.notifyItemInsertedFromChild(this, localPos)
    fun notifyItemRemoved(localPos: Int) = parent?.notifyItemRemovedFromChild(this, localPos)
    fun notifyItemChanged(localPos: Int) = parent?.notifyItemChangedFromChild(this, localPos)
    fun notifyRangeInserted(localStart: Int, count: Int) =
        parent?.notifyRangeInsertedFromChild(this, localStart, count)
    fun notifyItemRangeChanged(localStart: Int, count: Int, payload: Any? = null) =
        parent?.notifyItemRangeChangedFromChild(this, localStart, count, payload)

    // ========================= Diff Callback ===========================
    abstract fun areItemsTheSame(old: T, new: T): Boolean
    abstract fun areContentsTheSame(old: T, new: T): Boolean

    // ========================= ViewHolder ==============================
    abstract fun onCreateVH(parent: ViewGroup, viewType: Int): VH
    abstract fun onBindVH(holder: VH, position: Int)

    open fun getItemViewType(position: Int): Int = 0

    /** Internal: try create ViewHolder, log if viewType mismatch */
    internal fun tryCreate(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? =
        try {
            onCreateVH(parent, viewType)
        } catch (e: Exception) {
            Log.w("BaseItemAdapter", "ViewType $viewType not handled by this adapter")
            null
        }
}
