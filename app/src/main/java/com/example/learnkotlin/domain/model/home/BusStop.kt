package com.example.learnkotlin.domain.model.home

import android.os.Parcelable
import com.example.learnkotlin.presentation.base.NavData
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusStop(

    val gtfsId: String? = null,

    val name: String? = null,

    val code: String? = null,

    val description: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null,

    val locationType: String? = null,

    val platformCode: String? = null,

    val zoneId: String? = null,

    val vehicleMode: String? = null,

    val wheelchairBoarding: String? = null,

    val parentStation: ParentStation?,

    val routes: List<BusStopRoute>? = null
): Parcelable, NavData


@Parcelize
data class BusStopRoute(

    val gtfsId: String? = null,

    val shortName: String? = null,

    val description: String? = null,

    val mode: String? = null,

    val color: String? = null,

    val textColor: String? = null,

    val routeId: String? = null,
    val etaTime: String? = null,
    val licensePlate: String? = null,
): Parcelable, NavData


@Parcelize
data class ParentStation(

    val gtfsId: String? = null,

    val name: String? = null
): Parcelable, NavData