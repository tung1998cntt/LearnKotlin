package com.example.learnkotlin.core.network

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null // <- để T nằm trong response riêng từng API
)