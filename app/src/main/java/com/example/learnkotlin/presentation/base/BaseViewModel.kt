package com.example.learnkotlin.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnkotlin.core.network.ApiException
import com.example.learnkotlin.domain.base.BaseError
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.Event
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class BaseViewModel : ViewModel() {

    /** SupervisorJob cho nhiều coroutine độc lập */
    /** Scope chạy các command tuần tự */
    private val commandScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** Scope chạy các coroutine song song, heavy work */
    protected val workerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    protected var initData: NavData? = null

    override fun onCleared() {
        super.onCleared()
        commandScope.cancel()
        workerScope.cancel()
    }

    /** Command từ UI gửi vào ViewModel */
    private val _commands = MutableSharedFlow<Command>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    /** Event cho UI: Loading / Success / Error / Toast / Navigate … */
    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    /** Gửi command từ UI */
    fun sendCommand(command: Command) {
        viewModelScope.launch { _commands.emit(command) }
    }

    /** Gửi event UI */
    protected fun sendEvent(event: Event) {
        viewModelScope.launch { _events.emit(event) }
    }

    /** Collect command từ UI */
    open fun onInit() {
        commandScope.launch {
            _commands.collect { command ->
                when (command) {
                    is InitDataCommand<*> -> initData = command.data as? NavData
                    is ReadyCommand -> onReady()  // chạy sau khi nhận ReadyCommand
                    else -> handleCommand(command)
                }
            }
        }
    }

    protected open fun onReady() {}

    protected open fun showLoading() {
        sendEvent(UiEvent.Loading) // chỉ emit event, UI quyết định hiển thị
    }

    protected open fun hideLoading() {
        sendEvent(UiEvent.HideLoading) // UI sẽ hide loading
    }

    /** Subclass override để xử lý command */
    protected open fun handleCommand(command: Command) {}

    /**
     * Launch coroutine chuẩn:
     * - sendEvent(Loading) trước
     * - chạy block
     * - sendEvent(Success/Error)
     */

    protected fun launchWithLoading(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        showLoading: Boolean = true,
        block: suspend CoroutineScope.() -> Unit,
        customErrorHandler: ((BaseError) -> Unit)? = null
    ) {
        workerScope.launch {
            try {
                if (showLoading) showLoading()
                withContext(dispatcher) { block() }
            } catch (e: Exception) {
                // Map exception thành BaseError
                val error = when (e) {
                    is ApiException.NetworkError -> BaseError.NetworkError(e.message)
                    is ApiException.Unauthorized -> BaseError.Unauthorized(e.message)
                    is ApiException.Forbidden -> BaseError.Forbidden(e.message)
                    is ApiException.ServerError -> BaseError.ServerError(e.message, e.code)
                    else -> BaseError.UnknownError(e.message)
                }

                // Xử lý custom nếu có, không thì xử lý chung
                customErrorHandler?.invoke(error) ?: handleBaseError(error)
            } finally {
                if (showLoading) hideLoading()
            }
        }
    }


    protected open fun handleBaseError(error: BaseError) {
//        when (error) {
//            is BaseError.NetworkError -> sendEvent(UiEvent.ShowToast("No connection"))
//            is BaseError.ServerError -> sendEvent(UiEvent.ShowToast("Server error, try again"))
//            is BaseError.Unauthorized -> sendEvent(UiEvent.Logout)
//            else -> sendEvent(UiEvent.ShowToast(error.message ?: "Unknown error"))
    }


    protected open fun handleError(e: Exception) {
        when (e) {
//            is TokenExpiredException -> sendEvent(UiEvent.Logout)
//            is NetworkException -> sendEvent(UiEvent.ShowNetworkError)
            else -> sendEvent(UiEvent.Error(e.message, e))
        }
    }
}



