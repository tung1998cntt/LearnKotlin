package com.example.learnkotlin.domain.usecase.user

import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.repository.user.UserRepository
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository

) {

    suspend fun loadUsers(): ApiResult<List<User>> {
        return userRepository.getUsers()
    }
    suspend fun addUser(name: String): ApiResult<List<User>> = userRepository.addUser(name)
}