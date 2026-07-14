package com.example.learnkotlin.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("expired_at")
    val expiredAt: Long = 0L,

    @SerializedName("device_token")
    val deviceToken: String? = null,

    @SerializedName("is_admin")
    val isAdmin: Boolean = false,

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("role_id")
    val roleId: String? = null,

    @SerializedName("role_name")
    val roleName: String? = null,

    @SerializedName("role_type")
    val roleType: String? = null,

    @SerializedName("project_id")
    val projectId: String? = null,

    @SerializedName("org_id")
    val orgId: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("username")
    val username: String? = null
)