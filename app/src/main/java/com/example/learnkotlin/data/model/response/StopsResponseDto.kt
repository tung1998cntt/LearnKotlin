package com.example.learnkotlin.data.model.response

import com.google.gson.annotations.SerializedName

data class BusStopResponseDto(
    @SerializedName("stops")
    val stops: List<BusStopDto>? = null
)

data class BusStopDto(

    @SerializedName("gtfsId")
    val gtfsId: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("desc")
    val desc: String? = null,

    @SerializedName("lat")
    val lat: Double = 0.0,

    @SerializedName("lon")
    val lon: Double = 0.0,

    @SerializedName("locationType")
    val locationType: String? = null,

    @SerializedName("platformCode")
    val platformCode: String? = null,

    @SerializedName("zoneId")
    val zoneId: String? = null,

    @SerializedName("vehicleMode")
    val vehicleMode: String? = null,

    @SerializedName("wheelchairBoarding")
    val wheelchairBoarding: String? = null,

    @SerializedName("parentStation")
    val parentStation: ParentStationDto? = null,

    @SerializedName("routes")
    val routes: List<RouteDto>? = null
)

data class RouteDto(

    @SerializedName("gtfsId")
    val gtfsId: String? = null,

    @SerializedName("shortName")
    val shortName: String? = null,

    @SerializedName("desc")
    val desc: String? = null,

    @SerializedName("mode")
    val mode: String? = null,

    @SerializedName("color")
    val color: String? = null,

    @SerializedName("textColor")
    val textColor: String? = null,

    @SerializedName("routeId")
    val routeId: String? = null
)

data class ParentStationDto(

    @SerializedName("gtfsId")
    val gtfsId: String? = null,

    @SerializedName("name")
    val name: String? = null
)