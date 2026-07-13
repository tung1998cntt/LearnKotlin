package com.example.learnkotlin.domain.repository.route

import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.model.user.User

interface RouteRepository {
    suspend fun getUsers(): ApiResult<List<User>>
    suspend fun addUser(name: String): ApiResult<List<User>>
}