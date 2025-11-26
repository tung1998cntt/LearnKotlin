package com.example.learnkotlin.domain.usecase.user

import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.model.user.UserState
import com.example.learnkotlin.domain.repository.user.UserRepository

class UserUseCase(private val userRepository: UserRepository) {

    suspend fun loadUsers(): UserState {
        return when (val userState = userRepository.getUsers()) {
            is ApiResult.Success -> {
                UserState.GetUserSuccess(userState.data)
            }

            is ApiResult.Error -> {
                if (userState.message?.contains("specific business") == true) {
                    UserState.GetUserCustomError(result.message)
                } else {
                    UserState.GetUserError(baseError)
                }
            }
        }
    }

    suspend fun addUser(name: String): User = userRepository.addUser(name)
}