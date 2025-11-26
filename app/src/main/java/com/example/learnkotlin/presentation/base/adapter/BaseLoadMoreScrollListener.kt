package com.example.learnkotlin.presentation.base.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class BaseLoadMoreScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val threshold: Int = 5
) : RecyclerView.OnScrollListener() {

    private var isLoading = false
    private var isLastPage = false

    fun setLoading(loading: Boolean) {
        isLoading = loading
    }

    fun setLastPage(lastPage: Boolean) {
        isLastPage = lastPage
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy <= 0 || isLoading || isLastPage) return

        val totalItemCount = layoutManager.itemCount
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

        if (totalItemCount <= lastVisibleItem + threshold) {
            isLoading = true
            onLoadMore()
        }
    }

    abstract fun onLoadMore()
}