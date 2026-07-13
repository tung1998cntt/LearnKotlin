package com.example.learnkotlin.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnkotlin.core.network.ApiException
import com.example.learnkotlin.domain.base.BaseError
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.presentation.state.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException


abstract class BaseViewModel<S : UiState>: ViewModel() {


    protected var initData: NavData? = null

    override fun onCleared() {
        super.onCleared()
    }

    /** Command từ UI gửi vào ViewModel */
    private val _commands = MutableSharedFlow<Command>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    /** Event cho UI: Loading / Success / Error / Toast / Navigate … */
    private val _events = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val events: SharedFlow<Event> = _events.asSharedFlow()

    protected abstract fun createInitialState(): S
    private val _state by lazy {
        MutableStateFlow(createInitialState())
    }

    val state: StateFlow<S> = _state.asStateFlow()

    protected fun updateState(
        reducer: S.() -> S
    ) {
        _state.update(reducer)
    }

    protected fun setState(state: S) {
        _state.value = state
    }

    protected fun resetState() {
        _state.value = createInitialState()
    }

    /** Gửi command từ UI */
    fun sendCommand(command: Command) {
        viewModelScope.launch {
            _commands.emit(command)
        }
    }

    /** Gửi event UI */
    protected fun sendEvent(event: Event) {
        viewModelScope.launch { _events.emit(event) }
    }

    /** Collect command từ UI */
    open fun onInit() {
        viewModelScope.launch {
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
        viewModelScope.launch {
            try {
                if (showLoading) showLoading()
                withContext(dispatcher) { block() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val error = mapError(e)
                customErrorHandler?.invoke(error) ?: handleBaseError(error)
            } finally {
                if (showLoading) hideLoading()
            }
        }
    }

    protected suspend fun <T> execute(
        block: suspend () -> T,
        onSuccess: suspend (T)->Unit,
        onError: ((BaseError) -> Unit)? = null
    ) {
        try {

            onSuccess(block())

        } catch (e: CancellationException) {

            throw e

        } catch (e: Exception) {

            val error = mapError(e)

            onError?.invoke(error)
                ?: handleBaseError(error)
        }
    }

    /*
    * Chạy nhiều coroutine song song.
    * Bên trong phải dùng launch { } hoặc async { }.
    * Không nên gọi suspend function trực tiếp nếu muốn chạy song song.
    * */

    protected fun launchParallel(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        showLoading: Boolean = true,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        /*
        *  Chỉ dùng launch{} bên trong.
        * */
        return viewModelScope.launch {
            if (showLoading) showLoading()
            try {
                withContext(dispatcher) {
                    supervisorScope {
                        block()
                    }
                }

            } finally {
                if (showLoading) hideLoading()
            }
        }
    }

/*    launchParallel {

        launch {

            execute(

                block = {

                    repository.getUser()

                },

                onSuccess = {

                    _user.value = it

                },

                onError = {

                    sendEvent(UserError(it))

                }

            )

        }

        launch {

            execute(

                block = {

                    repository.getVehicle()

                },

                onSuccess = {

                    _vehicle.value = it

                },

                onError = {

                    sendEvent(VehicleError(it))

                }

            )

        }

    }*/

    private fun mapError(e: Exception): BaseError =
        when (e) {
            is ApiException.NetworkError ->
                BaseError.NetworkError("Network error")

            is ApiException.Unauthorized ->
                BaseError.Unauthorized(e.message)

            is ApiException.Forbidden ->
                BaseError.Forbidden(e.message)

            is ApiException.NotFound ->
                BaseError.NotFound(e.message)

            is ApiException.ServerError ->
                BaseError.ServerError(e.message, e.code)

            else ->
                BaseError.UnknownError(e.message)
        }


    protected open fun handleBaseError(error: BaseError) {
        /* Lỗi chung nhé*/
        when (error) {
            is BaseError.NetworkError -> sendEvent(DialogEvent.ShowError(error.message))
            is BaseError.ServerError -> sendEvent(DialogEvent.ShowError("Server error, try again"))
            is BaseError.Unauthorized -> sendEvent(DialogEvent.ShowError(error.message))
            is BaseError.Forbidden -> sendEvent(DialogEvent.ShowError(error.message))
            else -> sendEvent(DialogEvent.ShowError(error.message ?: "Unknown error"))
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



