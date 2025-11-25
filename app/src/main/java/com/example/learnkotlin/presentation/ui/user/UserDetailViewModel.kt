package com.example.learnkotlin.presentation.ui.user

import com.example.learnkotlin.core.logger.BaseLog
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.usecase.user.UserUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.presentation.base.UiEvent
import com.example.learnkotlin.presentation.model.user.ProductNavData
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class UserDetailViewModel: BaseViewModel(), KoinComponent  {

    override fun onReady() {
        super.onReady()
        val userNav = initData as? ProductNavData
        BaseLog.d("TUNG1998", userNav.toString())
    }

    override fun handleCommand(command: Command) {
        when (command) {
            is UserCommand.AddUser -> addUser(command)
            else -> Unit
        }
    }

    private fun addUser(command: UserCommand.AddUser) {
        launchWithLoading(
            showLoading = false,
            block = {
                val useCase: UserUseCase = get()
                command.name?.let { useCase.addUser(it) }
            },
            onResult = { users ->
                sendEvent(UserEvent.ShowUser(listOf(users) as? List<User>))
            },
            onError = { e ->
                handleError(e)
                sendEvent(UiEvent.Error("Add user failed: ${e.message}", e))
            }
        )
    }

}