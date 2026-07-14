package com.example.learnkotlin.domain.model.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteListResponse(
    val total: Int = 0,
    val offset: Int = 0,
    val limit: Int = 30,
    val routes: List<RouteItem>? = null
) : Parcelable

@Parcelize
data class RouteItem(
    val id: String? = null,
    val routeName: String? = null,
    val orgId: String? = null,
    val orgName: String? = null,
    val outboundDistance: String? = null,
    val inboundDistance: String? = null,
    val status: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val outboundStops: List<String>? = null,
    val inboundStops: List<String>? = null,
    val udPermission: Boolean = false,
    val auxiliaryImei: String? = null,
    val delayTime: Int = 0
) : Parcelable