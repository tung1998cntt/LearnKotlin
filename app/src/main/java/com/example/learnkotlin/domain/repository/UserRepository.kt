package com.example.learnkotlin.domain.repository

import com.example.learnkotlin.domain.model.User

interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun addUser(name: String): User
}
