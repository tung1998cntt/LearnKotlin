package com.example.learnkotlin.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.presentation.model.NavData
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
        block: suspend CoroutineScope.() -> Any?,
        onResult: (result: Any?) -> Unit,
        onError: (Exception) -> Unit = { handleError(it) }
    ) {
        workerScope.launch {
            try {
                if (showLoading) showLoading()
                val result = withContext(dispatcher) { block() }
                onResult(result)
            } catch (e: Exception) {
                onError(e)
            } finally {
                if (showLoading) hideLoading()
            }
        }
    }


    protected open fun handleError(e: Exception) {
        when (e) {
//            is TokenExpiredException -> sendEvent(UiEvent.Logout)
//            is NetworkException -> sendEvent(UiEvent.ShowNetworkError)
            else -> sendEvent(UiEvent.Error(e.message, e))
        }
    }
}



