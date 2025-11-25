package com.example.learnkotlin.data.repository.user

import com.example.learnkotlin.domain.model.user.User
import com.example.learnkotlin.domain.repository.user.UserRepository
import kotlinx.coroutines.delay

class UserRepositoryImpl : UserRepository {


    override suspend fun getUsers(): List<User> {
        delay(3000)
        val list = mutableListOf<User>()
        list.add(User(1, "Bach Tung"))
        list.add(User(2, "Mai Huong"))
        return list
    }

    override suspend fun addUser(name: String): User {
        delay(3000)
        val list = mutableListOf<User>()
        val user = User(id = 0 + 1, name = name)
        list.add(user)
        return user
    }
}