package com.example.learnkotlin.data.repository

import com.example.learnkotlin.domain.model.User
import com.example.learnkotlin.domain.repository.UserRepository
import kotlinx.coroutines.delay

class UserRepositoryImpl : UserRepository {

    private val list = mutableListOf<User>()

    override suspend fun getUsers(): List<User> {
        delay(500)
        return list
    }

    override suspend fun addUser(name: String): User {
        delay(300)
        val user = User(id = list.size + 1, name = name)
        list.add(user)
        return user
    }
}