package com.example.learnkotlin.presentation.ui.user_management

import com.example.learnkotlin.domain.model.User
import com.example.learnkotlin.domain.usecase.UserUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.presentation.base.UiEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class UserViewModel : BaseViewModel(), KoinComponent {


    override fun handleCommand(command: Command) {
        when (command) {
            is UserCommand.LoadUsers -> loadUsers()
            is UserCommand.AddUser -> addUser(command)
            else -> Unit
        }
    }

    private fun addUser(command: UserCommand.AddUser) {
        launchWithLoading(
            showLoading = false, // ví dụ không hiển thị loading
            block = {
                val useCase: UserUseCase = get()
                command.name?.let { useCase.addUser(it) }
            },
            onResult = { user ->
                sendEvent(UserEvent.ShowUser(user as User?))
            },
            onError = { e ->
                handleError(e)
                sendEvent(UiEvent.Error("Add user failed: ${e.message}", e))
            }
        )
    }

    private fun loadUsers() {
        launchWithLoading(
            showLoading = true,  // có thể đặt false nếu không muốn loading
            block = {
                val useCase: UserUseCase = get()
                useCase.loadUsers()
            },
            onResult = { users ->
                sendEvent(UserEvent.ShowUser(users as User?))
            },
            onError = { e ->
                sendEvent(UiEvent.Error("Load users failed: ${e.message}", e))
            }
        )
    }
}


