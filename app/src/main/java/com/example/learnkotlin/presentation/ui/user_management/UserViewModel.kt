package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.presentation.base.BaseViewModel
import kotlinx.coroutines.delay

class UserViewModel : BaseViewModel() {

    override suspend fun handleCommand(command: Any) {
        when (command) {
            is UserCommand.LoadUser -> {
                sendEvent(UserEvent.ShowLoading)
                delay(1000) // giả lập network
                updateStateSuccess("User name: John Doe, id=${command.id}")
                sendEvent(UserEvent.ShowUser("John Doe"))
            }

            is UserCommand.RefreshUser -> {
                sendEvent(UserEvent.ShowLoading)
                delay(500)
                updateStateSuccess("User refreshed!")
            }
            is UserCommand.GoToDetail -> {

            }

            else -> {
                sendEvent(UserEvent.ShowError("Unknown command"))
            }
        }
    }
}
