package com.example.learnkotlin.domain.repository.profile

import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.model.user.User

interface ProfileRepository {
    suspend fun getUsers(): ApiResult<List<User>>
    suspend fun addUser(name: String): ApiResult<List<User>>
}