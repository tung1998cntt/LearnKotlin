package com.example.learnkotlin.domain.model.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NearbyArrivalResponse(
    val origin: Origin?,
    val radiusMeters: Int,
    val pagination: Pagination?,
    val stops: List<Stop>
): Parcelable

@Parcelize
data class Origin(
    val lat: Double,
    val lon: Double
) : Parcelable

@Parcelize
data class Pagination(
    val page: Int,
    val pageSize: Int,
    val returnedItems: Int,
    val hasNext: Boolean
) : Parcelable

@Parcelize
data class Stop(
    val stopId: String,
    val stopName: String,
    val lat: Double,
    val lon: Double,
    val distanceMeters: Double,
    val arrivals: List<Arrival>
) : Parcelable

@Parcelize
data class Arrival(
    val routeId: String,
    val routeShortName: String,
    val routeLongName: String,
    val tripId: String,
    val headsign: String,
    val scheduledArrival: String,
    val estimatedArrival: String,
    val delaySeconds: Int,
    val minutesToArrival: Int,
    val realtime: Boolean,
    val vehicles: List<Vehicle>
) : Parcelable

@Parcelize
data class Vehicle(
    val vehicleId: String,
    val licensePlate: String,
    val lat: Double,
    val lon: Double,
    val heading: Double,
    val speedKmh: Double,
    val distanceKm: Double,
    val distanceSource: String,
    val etaSeconds: Int,
    val etaTime: String,
    val walkDistanceKm: Double,
    val walkEtaSeconds: Int,
    val walkEtaTime: String,
    val walkSource: String,
    val hasLocation: Boolean
) : Parcelable