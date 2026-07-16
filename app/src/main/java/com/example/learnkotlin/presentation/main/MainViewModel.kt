package com.example.learnkotlin.presentation.main

import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.model.home.LoginRequest
import com.example.learnkotlin.domain.usecase.home.HomeUseCase
import com.example.learnkotlin.presentation.base.BaseViewModel
import com.example.learnkotlin.presentation.home.HomeEvent
import com.example.learnkotlin.presentation.state.EmptyState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase
) : BaseViewModel<EmptyState>() {
    override fun createInitialState() = EmptyState

    fun login(
        username: String? = null,
        password: String? = null
    ) {

        launchWithLoading(
            showLoading = false,
            block = {
                when (
                    val result = homeUseCase.login(
                        LoginRequest(
//                            username = "anhnt650",
//                            password = "123456aC@"

                            username = "suriname",
                            password = "123456aA@"
                        )
                    )
                ) {
                    is ApiResult.Success -> {
                        /* To do*/

                    }
                    is ApiResult.Error -> {
                        sendEvent(
                            HomeEvent.ShowError(
                                result.message ?: "Unknown error"
                            )
                        )
                    }

                }
            },
            customErrorHandler = null
        )
    }

}