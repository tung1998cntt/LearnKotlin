package com.example.learnkotlin.data.model.response

import com.google.gson.annotations.SerializedName

data class AreaDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
)