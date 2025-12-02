package com.example.learnkotlin.domain.repository.user

import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.model.user.User

interface UserRepository {
    suspend fun getUsers(): ApiResult<List<User>>
    suspend fun addUser(name: String): ApiResult<User>
}