package com.example.learnkotlin.domain.usecase.route

import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.repository.home.HomeRepository
import com.example.learnkotlin.domain.repository.route.RouteRepository
import javax.inject.Inject

class RouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository

) {

    suspend fun loadUsers(): ApiResult<List<User>> {
        return routeRepository.getUsers()
    }
    suspend fun addUser(name: String): ApiResult<List<User>> = routeRepository.addUser(name)
}