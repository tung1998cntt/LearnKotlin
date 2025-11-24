package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.domain.model.User
import com.example.learnkotlin.domain.base.Event

sealed class UserEvent: Event {
    data class ShowUser(val name: User? = null) : UserEvent()
    data class ShowError(val message: String) : UserEvent()
}