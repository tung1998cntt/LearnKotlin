package com.example.learnkotlin.domain.usecase

import com.example.learnkotlin.domain.model.User
import com.example.learnkotlin.domain.repository.UserRepository

class UserUseCase(private val userRepository: UserRepository) {

    suspend fun loadUsers(): List<User> = userRepository.getUsers()

    suspend fun addUser(name: String): User = userRepository.addUser(name)
}