    package com.example.learnkotlin.presentation.base

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import kotlinx.coroutines.flow.*
    import kotlinx.coroutines.launch

    abstract class BaseViewModel : ViewModel() {

        private val _commands = MutableSharedFlow<Any>(extraBufferCapacity = 64)
        val commands: SharedFlow<Any> = _commands.asSharedFlow()

        protected val _state = MutableStateFlow<BaseUiState>(BaseUiState.Empty)
        val state: StateFlow<BaseUiState> get() = _state.asStateFlow()

        private val _events = MutableSharedFlow<Any>()
        val events: SharedFlow<Any> = _events.asSharedFlow()

        /** UI gửi command */
        fun sendCommand(command: Any) {
            viewModelScope.launch { _commands.emit(command) }
        }

        /** Gửi Event về View */
        protected fun sendEvent(event: Any) {
            viewModelScope.launch { _events.emit(event) }
        }


        open fun onInitViewModel(navData: UiCommand?) {
            navData?.let { sendCommand(it) }
        }



        /** Init: tự động collect command */
        open fun onInit() {
            viewModelScope.launch {
                _commands.collectLatest { command ->
                    _state.value = BaseUiState.Loading
                    try {
                        if (command !is NavigationCommand) _state.value = BaseUiState.Loading
                        handleCommand(command)
                    } catch (e: Exception) {
                        _state.value = BaseUiState.Error(e.message, e)
                    }
                }
            }
        }

        /** Subclass override để xử lý command */
        protected abstract suspend fun handleCommand(command: Any)

        /** Helper cập nhật state */
        protected fun updateStateSuccess(data: Any) {
            _state.value = BaseUiState.Success(data)
        }

        protected fun updateStateEmpty() {
            _state.value = BaseUiState.Empty
        }

        protected fun updateStateError(message: String?, throwable: Throwable? = null) {
            _state.value = BaseUiState.Error(message, throwable)
        }
    }
