package com.example.learnkotlin.domain.model.user

sealed class UserState {
    data class GetUserSuccess(val users: List<User>? = null) : UserState()
    data class GetUserError(val errorDescription: String? = null) : UserState()
}