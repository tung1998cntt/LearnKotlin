package com.example.learnkotlin.data.repository.user

import com.example.learnkotlin.core.extensions.safeApiCall
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.core.network.ApiService
import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.repository.user.UserRepository
import kotlinx.coroutines.delay

class UserRepositoryImpl(private val apiService: ApiService) : UserRepository {


    override suspend fun getUsers(): ApiResult<List<User>> {
        delay(3000)
        return safeApiCall { apiService.getUsers() }
    }

    override suspend fun addUser(name: String): User {
        delay(3000)
        val list = mutableListOf<User>()
        val user = User(id = 0 + 1, name = name)
        list.add(user)
        return user
    }
}