package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.domain.model.User
import com.example.learnkotlin.presentation.base.UiCommand

sealed class UserCommand : UiCommand {
    data class LoadUser(val id: Int) : UserCommand()
    object RefreshUser : UserCommand()


}