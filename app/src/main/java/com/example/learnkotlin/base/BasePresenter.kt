package com.example.learnkotlin.base

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

interface BasePresenter<V : BaseView> {
    fun attachView(view: V)
    fun detachView()
}


open class BasePresenterImpl<V : BaseView> : BasePresenter<V> {


    protected var view: V? = null
    private var job: Job? = null
    protected val scope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + (job ?: Job()))


    protected fun <T> launchTask(
        showLoading: Boolean = true,
        task: suspend () -> T,
        onSuccess: (T) -> Unit
    ) {
        scope.launch {
            if (showLoading) view?.showLoading(true)
            try {
                val result = withContext(Dispatchers.IO) { task() }
                if (isActive) {
                    view?.let { onSuccess(result) }
                }
            } catch (e: CancellationException) {
                // coroutine bị hủy — bỏ qua
            } catch (e: Exception) {
                view?.showError(e.message ?: "Unknown error")
            } finally {
                if (showLoading) view?.showLoading(false)
            }
        }
    }

    override fun attachView(view: V) {
        this.view = view
        this.job = Job()
    }

    override fun detachView() {
        this.view = null
        job?.cancel()
        job = null
    }

    protected fun isViewAttached() = view != null
}