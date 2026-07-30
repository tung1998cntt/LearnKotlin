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
    val routes: List<RouteItemDto>? = null
)

data class RouteItemDto(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("route_name")
    val routeName: String? = null,

    @SerializedName("org_id")
    val orgId: String? = null,

    @SerializedName("org_name")
    val orgName: String? = null,

    @SerializedName("outbound_distance")
    val outboundDistance: String? = null,

    @SerializedName("inbound_distance")
    val inboundDistance: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("created_at")
    val createdAt: Long = 0L,

    @SerializedName("updated_at")
    val updatedAt: Long= 0L,

    @SerializedName("outbound_stops")
    val outboundStops: List<String>? = null,

    @SerializedName("inbound_stops")
    val inboundStops: List<String>? = null,

    @SerializedName("ud_permission")
    val udPermission: Boolean = false,

    @SerializedName("auxiliary_imei")
    val auxiliaryImei: String? = null,

    @SerializedName("delay_time")
    val delayTime: Int = 0
)