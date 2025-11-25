package com.example.learnkotlin.presentation.base

import com.example.learnkotlin.domain.base.Event

sealed class UiEvent : Event {
    object Loading : UiEvent()
    object HideLoading : UiEvent()

    data class Error(val message: String?, val throwable: Throwable? = null) : UiEvent()
}