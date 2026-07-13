package com.example.learnkotlin.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(

    @SerializedName("token")
    val token: String,

    @SerializedName("expired_at")
    val expiredAt: Long,

    @SerializedName("device_token")
    val deviceToken: String?,

    @SerializedName("is_admin")
    val isAdmin: Boolean,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("role_id")
    val roleId: String,

    @SerializedName("role_name")
    val roleName: String,

    @SerializedName("role_type")
    val roleType: String,

    @SerializedName("project_id")
    val projectId: String,

    @SerializedName("org_id")
    val orgId: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("username")
    val username: String
)