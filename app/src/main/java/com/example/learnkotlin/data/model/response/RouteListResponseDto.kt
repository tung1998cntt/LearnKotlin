package com.example.learnkotlin.data.model.response

import com.google.gson.annotations.SerializedName

data class RouteListResponseDto(

    @SerializedName("total")
    val total: Int,

    @SerializedName("offset")
    val offset: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("routes")
    val routes: List<RouteItemDto>
)

data class RouteItemDto(

    @SerializedName("id")
    val id: String,

    @SerializedName("route_name")
    val routeName: String,

    @SerializedName("org_id")
    val orgId: String,

    @SerializedName("org_name")
    val orgName: String,

    @SerializedName("outbound_distance")
    val outboundDistance: String,

    @SerializedName("inbound_distance")
    val inboundDistance: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: Long,

    @SerializedName("updated_at")
    val updatedAt: Long,

    @SerializedName("outbound_stops")
    val outboundStops: List<String>?,

    @SerializedName("inbound_stops")
    val inboundStops: List<String>?,

    @SerializedName("ud_permission")
    val udPermission: Boolean,

    @SerializedName("auxiliary_imei")
    val auxiliaryImei: String?,

    @SerializedName("delay_time")
    val delayTime: Int
)