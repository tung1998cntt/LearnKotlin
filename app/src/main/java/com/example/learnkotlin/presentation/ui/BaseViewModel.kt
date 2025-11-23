package com.example.learnkotlin.presentation.ui

import UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {

    open fun <T> launchData(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend () -> T,
        onState: (UiState<T>) -> Unit
    ) {
        viewModelScope.launch {
            onState(UiState.Loading)
            try {
                val result = withContext(dispatcher) { block() }
                onState(UiState.Success(result))
            } catch (e: Exception) {
                onState(UiState.Error(e))
                e.printStackTrace()
            }
        }
    }
    /*
    *
    * Xin chao
    * */
}