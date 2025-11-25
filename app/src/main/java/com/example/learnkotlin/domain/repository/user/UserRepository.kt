package com.example.learnkotlin.domain.repository.user

import com.example.learnkotlin.domain.model.user.User

interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun addUser(name: String): User
}