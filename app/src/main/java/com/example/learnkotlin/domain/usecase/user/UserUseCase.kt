package com.example.learnkotlin.domain.usecase.user

import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.repository.user.UserRepository

class UserUseCase(private val userRepository: UserRepository) {

    suspend fun loadUsers(): List<User> = userRepository.getUsers()

    suspend fun addUser(name: String): User = userRepository.addUser(name)
}