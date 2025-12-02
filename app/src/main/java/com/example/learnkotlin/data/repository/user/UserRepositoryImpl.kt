package com.example.learnkotlin.data.repository.user

import com.example.learnkotlin.core.extensions.safeApiCall
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.core.network.ApiService
import com.example.learnkotlin.core.network.BaseResponse
import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.repository.user.UserRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService

) : UserRepository {
    override suspend fun getUsers(): ApiResult<List<User>> {
        delay(3000)
        return safeApiCall { apiService.getUsers() }
    }

    override suspend fun addUser(name: String): ApiResult<List<User>> {
        delay(3000)
        return safeApiCall {
            val list = mutableListOf<User>()
            val user = User(id = 0 + 1, name = name)
            list.add(user)
            BaseResponse(
                code = "200",
                message = "OK",
                data = list
            )
        }
    }
}