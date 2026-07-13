package com.example.learnkotlin.domain.model.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteListResponse(
    val total: Int,
    val offset: Int,
    val limit: Int,
    val routes: List<RouteItem>
) : Parcelable

@Parcelize
data class RouteItem(
    val id: String,
    val routeName: String,
    val orgId: String,
    val orgName: String,
    val outboundDistance: String,
    val inboundDistance: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val outboundStops: List<String>?,
    val inboundStops: List<String>?,
    val udPermission: Boolean,
    val auxiliaryImei: String?,
    val delayTime: Int
) : Parcelable