package com.example.learnkotlin.domain.usecase.profile

import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.repository.route.RouteRepository
import javax.inject.Inject

class ProfileUseCase @Inject constructor(
    private val profileRepository: RouteRepository

) {

    suspend fun loadUsers(): ApiResult<List<User>> {
        return profileRepository.getUsers()
    }
    suspend fun addUser(name: String): ApiResult<List<User>> = profileRepository.addUser(name)
}