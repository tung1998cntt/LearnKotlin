package com.example.learnkotlin.presentation.base

sealed class BaseUiState {
    object Loading : BaseUiState()
    data class Success(val data: Any) : BaseUiState()
    data class Error(val message: String?, val throwable: Throwable? = null) : BaseUiState()
    object Empty : BaseUiState()
}