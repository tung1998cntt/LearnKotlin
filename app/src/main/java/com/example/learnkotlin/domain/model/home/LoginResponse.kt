package com.example.learnkotlin.domain.model.home

data class LoginResponse(
    val token: String,
    val expiredAt: Long,
    val deviceToken: String?,
    val isAdmin: Boolean,
    val userId: String,
    val roleId: String,
    val roleName: String,
    val roleType: String,
    val projectId: String,
    val orgId: String?,
    val name: String,
    val phone: String,
    val username: String
)