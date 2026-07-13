package com.example.learnkotlin.data.model.response

import com.google.gson.annotations.SerializedName

data class GeocodingResponseDto (

    @SerializedName("features")
    val features: List<FeatureDto> = emptyList(),

    @SerializedName("query")
    val query: List<String> = emptyList(),

    @SerializedName("type")
    val type: String = ""
)


data class FeatureDto(

    @SerializedName("place_name")
    val placeName: String? = null,

    @SerializedName("center")
    val center: List<Double>? = null
)