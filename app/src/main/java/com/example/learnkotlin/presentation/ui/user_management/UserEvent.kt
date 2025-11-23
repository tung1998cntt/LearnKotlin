package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.presentation.base.UiEvent

sealed class UserEvent: UiEvent {
    data class ShowUser(val name: String) : UserEvent()
    data class ShowError(val message: String) : UserEvent()
    object ShowLoading : UserEvent()
}