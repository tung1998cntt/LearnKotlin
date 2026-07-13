package com.example.learnkotlin.domain.model.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoutePlan(

    val requestId: String? = null,

    val from: RouteLocation? = null,

    val to: RouteLocation? = null,

    val itineraries: List<Itinerary>? = null
): Parcelable


@Parcelize
data class RouteLocation(

    val name: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null
): Parcelable

@Parcelize
data class Itinerary(

    val startTime: Long? = null,

    val endTime: Long? = null,

    val duration: Long? = null,

    val waitingTime: Long? = null,

    val distance: Double? = null,

    val transfers: Int? = null,

    val legs: List<RouteLeg>? = null
): Parcelable

@Parcelize
data class RouteLeg(

    val transitLeg: Boolean? = null,

    val startTime: Long? = null,

    val endTime: Long? = null,

    val mode: String? = null,

    val duration: Long? = null,

    val routeName: String? = null,

    val routeId: String? = null,

    val headsign: String? = null,

    val from: RouteLocation? = null,

    val to: RouteLocation? = null,

    val distance: Double? = null,

    val agencyId: String? = null,

    val agencyName: String? = null
): Parcelable