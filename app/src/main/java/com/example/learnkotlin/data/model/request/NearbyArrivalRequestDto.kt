package com.example.learnkotlin.data.model.request

import com.google.gson.annotations.SerializedName

data class NearbyArrivalRequestDto(
    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,

    @SerializedName("radiusMeters")
    val radiusMeters: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("time")
    val time: String? = null,

    @SerializedName("page")
    val page: Int? = null
)