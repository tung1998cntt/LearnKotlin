package com.example.learnkotlin.data.model.request

import com.google.gson.annotations.SerializedName

data class RouteListRequestDto(
    @SerializedName("route_ids")
    val routeIds: List<String>,

    @SerializedName("status")
    val status: List<String>,

    @SerializedName("search")
    val search: String? = null,

//    @SerializedName("start_time")
//    val startTime: Long? = null,
//
//    @SerializedName("end_time")
//    val endTime: Long? = null
)