package com.example.learnkotlin.presentation.ui.user

import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.usecase.user.UserUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.presentation.base.UiEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class UserViewModel : BaseViewModel(), KoinComponent {


    override fun handleCommand(command: Command) {
        when (command) {
            is UserCommand.LoadUsers -> loadUsers()
            else -> Unit
        }
    }

    private fun loadUsers() {
        launchWithLoading(
            showLoading = true,
            block = {
                val useCase: UserUseCase = get()
                useCase.loadUsers()
            },
            onResult = { users ->
                sendEvent(UserEvent.ShowUser(users as? List<User>))
                sendEvent(UserEvent.ShowAlert)
            },
            onError = { e ->
                sendEvent(UiEvent.Error("Load users failed: ${e.message}", e))
            }
        )
    }
}


