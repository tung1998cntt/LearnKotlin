package com.example.learnkotlin.data.model.request

import com.google.gson.annotations.SerializedName

data class RoutePlanRequestDto(

    @SerializedName("fromLat")
    val fromLat: Double,

    @SerializedName("fromLon")
    val fromLon: Double,

    @SerializedName("toLat")
    val toLat: Double,

    @SerializedName("toLon")
    val toLon: Double
)
