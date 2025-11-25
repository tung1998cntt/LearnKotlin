package com.example.learnkotlin.presentation.ui.user

import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.base.Event

sealed class UserEvent : Event {
    data class ShowUser(val user: List<User>? = null) : UserEvent()

    data class ShowError(val message: String) : UserEvent()
    object ShowAlert : UserEvent()
}