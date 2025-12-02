package com.example.learnkotlin.presentation.base

import com.example.learnkotlin.domain.base.Event

sealed class DialogEvent : Event {
    data class ShowError(val message: String? = null) : UiEvent()
}