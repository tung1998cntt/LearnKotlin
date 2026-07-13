package com.example.learnkotlin.data.model.response

import com.google.gson.annotations.SerializedName

data class RoutePlanResponseDto(

    @SerializedName("requestId")
    val requestId: String? = null,

    @SerializedName("from")
    val from: LocationDto? = null,

    @SerializedName("to")
    val to: LocationDto? = null,

    @SerializedName("itineraries")
    val itineraries: List<ItineraryDto>? = null
)

data class LocationDto(

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("lon")
    val lon: Double? = null
)

data class ItineraryDto(

    @SerializedName("startTime")
    val startTime: Long? = null,

    @SerializedName("endTime")
    val endTime: Long? = null,

    @SerializedName("duration")
    val duration: Long? = null,

    @SerializedName("waitingTime")
    val waitingTime: Long? = null,

    @SerializedName("distance")
    val distance: Double? = null,

    @SerializedName("transfers")
    val transfers: Int? = null,

    @SerializedName("legs")
    val legs: List<LegDto>? = null
)

data class LegDto(

    @SerializedName("transitLeg")
    val transitLeg: Boolean? = null,

    @SerializedName("startTime")
    val startTime: Long? = null,

    @SerializedName("endTime")
    val endTime: Long? = null,

    @SerializedName("mode")
    val mode: String? = null,

    @SerializedName("duration")
    val duration: Long? = null,

    @SerializedName("routeName")
    val routeName: String? = null,

    @SerializedName("routeId")
    val routeId: String? = null,

    @SerializedName("headsign")
    val headsign: String? = null,

    @SerializedName("from")
    val from: LocationDto? = null,

    @SerializedName("to")
    val to: LocationDto? = null,

    @SerializedName("distance")
    val distance: Double? = null,

    @SerializedName("agencyId")
    val agencyId: String? = null,

    @SerializedName("agencyName")
    val agencyName: String? = null
)
