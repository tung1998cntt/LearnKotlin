package com.example.learnkotlin.presentation.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView

class LoadMoreAdapter : BaseItemAdapter<LoadMoreAdapter.LoadMoreVH, Any>() {

    var isLoading = false
        set(value) {
            field = value
            if (value) notifyRangeInserted(items.size, 1)
            else notifyItemRemoved(items.size)
        }

    override fun areItemsTheSame(old: Any, new: Any) = false
    override fun areContentsTheSame(old: Any, new: Any) = false

    override fun onCreateVH(parent: ViewGroup, viewType: Int): LoadMoreVH {
        val view = LayoutInflater.from(parent.context).inflate(
            android.R.layout.simple_list_item_1, parent, false
        )
        val progressBar = ProgressBar(parent.context)
        (view as ViewGroup).addView(progressBar)
        return LoadMoreVH(view)
    }

    override fun onBindVH(holder: LoadMoreVH, position: Int) { /* nothing */ }

    override fun getItemCount(): Int = if (isLoading) 1 else 0

    class LoadMoreVH(view: View) : RecyclerView.ViewHolder(view)
}