package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.domain.base.Command

sealed class UserCommand: Command{
    object LoadUsers : UserCommand()
    data class AddUser(val name: String? = null) : UserCommand()
}
