package com.example.learnkotlin.data.model.response

import com.google.gson.annotations.SerializedName

data class RouteDetailResponseDto(

    @SerializedName("id")
    val id: String?= null,

    @SerializedName("route_name")
    val routeName: String?= null,

    @SerializedName("org_id")
    val orgId: String?= null,

    @SerializedName("org_name")
    val orgName: String?= null,

    @SerializedName("outbound_distance")
    val outboundDistance: String?= null,

    @SerializedName("inbound_distance")
    val inboundDistance: String?= null,

    @SerializedName("status")
    val status: String?= null,

    @SerializedName("created_at")
    val createdAt: Long,

    @SerializedName("updated_at")
    val updatedAt: Long,

    @SerializedName("outbound_stops")
    val outboundStops: List<RouteStopDto>?= null,

    @SerializedName("inbound_stops")
    val inboundStops: List<RouteStopDto>? = null,

    @SerializedName("ud_permission")
    val udPermission: Boolean,

    @SerializedName("auxiliary_imei")
    val auxiliaryImei: String?= null,

    @SerializedName("delay_time")
    val delayTime: Int
)

data class RouteStopDto(

    @SerializedName("id")
    val id: String?= null,

    @SerializedName("stop_name")
    val stopName: String?= null,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("notification_code")
    val notificationCode: String?= null,

    @SerializedName("stop_order")
    val stopOrder: String?= null,

    @SerializedName("path_points")
    val pathPoints: List<List<Double>>? = null
)