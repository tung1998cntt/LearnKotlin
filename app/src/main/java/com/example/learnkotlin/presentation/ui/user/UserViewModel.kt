package com.example.learnkotlin.presentation.ui.user

import com.example.learnkotlin.core.network.ApiException
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.usecase.user.UserUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import javax.inject.Inject

@HiltViewModel
class UserViewModel : BaseViewModel() {

    @Inject
    lateinit var useCase: UserUseCase

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
                when (val result = useCase.loadUsers()) { // trả ApiResult
                    is ApiResult.Success -> sendEvent(UserEvent.ShowUser(result.data))
                    is ApiResult.Error -> throw ApiException.ServerError(result.message ?: "Unknown") // ném lỗi để launchWithLoading catch
                }
            },
            customErrorHandler = {

            }
        )
    }
}


