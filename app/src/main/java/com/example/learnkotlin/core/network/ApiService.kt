package com.example.learnkotlin.core.network

import com.example.learnkotlin.domain.model.user.User
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    suspend fun getUsers(): BaseResponse<List<User>>
}