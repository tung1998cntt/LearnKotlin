package com.example.learnkotlin.presentation.ui.user

import com.example.learnkotlin.core.logger.BaseLog
import com.example.learnkotlin.core.network.ApiException
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.usecase.user.UserUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.presentation.model.user.ProductNavData
import com.example.learnkotlin.presentation.state.EmptyState
import javax.inject.Inject

class UserDetailViewModel @Inject constructor(
    private val useCase: UserUseCase
): BaseViewModel<EmptyState>()  {
    override fun createInitialState(): EmptyState = EmptyState
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
                when (val result = useCase.addUser(command.name ?: "")) { // trả ApiResult
                    is ApiResult.Success -> sendEvent(UserEvent.ShowUser(result.data))
                    is ApiResult.Error -> throw ApiException.ServerError(
                        result.message ?: "Unknown"
                    ) // ném lỗi để launchWithLoading catch
                }
            },
            customErrorHandler = {

            }
        )
    }

}