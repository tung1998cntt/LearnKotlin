package com.example.learnkotlin.data.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("identifier")
    val username: String,

    @SerializedName("password")
    val password: String
)