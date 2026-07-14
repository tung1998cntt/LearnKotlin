package com.example.learnkotlin.domain.model.home

data class RouteDetail(

    val id: String? = null,

    val routeName: String? = null,

    val orgId: String? = null,

    val orgName: String? = null,

    val outboundDistance: String? = null,

    val inboundDistance: String? = null,

    val status: String? = null,

    val createdAt: Long,

    val updatedAt: Long,

    val outboundStops: List<RouteStop>? = null,

    val inboundStops: List<RouteStop>? = null,

    val udPermission: Boolean,

    val auxiliaryImei: String? = null,

    val delayTime: Int
)

data class RouteStop(

    val id: String? = null,

    val stopName: String? = null,

    val latitude: Double,

    val longitude: Double,

    val notificationCode: String? = null,

    val stopOrder: String? = null,

    val pathPoints: List<RoutePoint>? = null
)

data class RoutePoint(

    val longitude: Double,

    val latitude: Double

)