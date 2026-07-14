package com.example.learnkotlin.domain.model.home

data class LoginResponse(
    val token: String? = null,
    val expiredAt: Long = 0L,
    val deviceToken: String? = null,
    val isAdmin: Boolean = false,
    val userId: String? = null,
    val roleId: String? = null,
    val roleName: String? = null,
    val roleType: String? = null,
    val projectId: String? = null,
    val orgId: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val username: String? = null
)