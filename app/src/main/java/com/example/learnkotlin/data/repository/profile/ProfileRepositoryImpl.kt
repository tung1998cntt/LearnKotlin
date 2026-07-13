package com.example.learnkotlin.data.repository.profile

import com.example.learnkotlin.core.extensions.safeApiCall
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.core.network.ApiService
import com.example.learnkotlin.core.network.BaseResponse
import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.repository.home.HomeRepository
import com.example.learnkotlin.domain.repository.profile.ProfileRepository
import com.example.learnkotlin.domain.repository.route.RouteRepository
import com.example.learnkotlin.domain.repository.user.UserRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ApiService

) : ProfileRepository {
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