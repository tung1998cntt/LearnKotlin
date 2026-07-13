package com.example.learnkotlin.data.model.response

import com.google.gson.annotations.SerializedName

data class NearbyArrivalResponseDto(

    @SerializedName("origin")
    val origin: OriginDto?,

    @SerializedName("radiusMeters")
    val radiusMeters: Int,

    @SerializedName("pagination")
    val pagination: PaginationDto?,

    @SerializedName("stops")
    val stops: List<StopDto>
)

data class OriginDto(
    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double
)

data class PaginationDto(
    @SerializedName("page")
    val page: Int,

    @SerializedName("pageSize")
    val pageSize: Int,

    @SerializedName("returnedItems")
    val returnedItems: Int,

    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class StopDto(

    @SerializedName("stopId")
    val stopId: String,

    @SerializedName("stopName")
    val stopName: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,

    @SerializedName("distanceMeters")
    val distanceMeters: Double,

    @SerializedName("arrivals")
    val arrivals: List<ArrivalDto>
)

data class ArrivalDto(

    @SerializedName("routeId")
    val routeId: String,

    @SerializedName("routeShortName")
    val routeShortName: String,

    @SerializedName("routeLongName")
    val routeLongName: String,

    @SerializedName("tripId")
    val tripId: String,

    @SerializedName("headsign")
    val headsign: String,

    @SerializedName("scheduledArrival")
    val scheduledArrival: String,

    @SerializedName("estimatedArrival")
    val estimatedArrival: String,

    @SerializedName("delaySeconds")
    val delaySeconds: Int,

    @SerializedName("minutesToArrival")
    val minutesToArrival: Int,

    @SerializedName("realtime")
    val realtime: Boolean,

    @SerializedName("vehicles")
    val vehicles: List<VehicleDto>
)

data class VehicleDto(

    @SerializedName("vehicleId")
    val vehicleId: String,

    @SerializedName("licensePlate")
    val licensePlate: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,

    @SerializedName("heading")
    val heading: Double,

    @SerializedName("speedKmh")
    val speedKmh: Double,

    @SerializedName("distanceKm")
    val distanceKm: Double,

    @SerializedName("distanceSource")
    val distanceSource: String,

    @SerializedName("etaSeconds")
    val etaSeconds: Int,

    @SerializedName("etaTime")
    val etaTime: String,

    @SerializedName("walkDistanceKm")
    val walkDistanceKm: Double,

    @SerializedName("walkEtaSeconds")
    val walkEtaSeconds: Int,

    @SerializedName("walkEtaTime")
    val walkEtaTime: String,

    @SerializedName("walkSource")
    val walkSource: String,

    @SerializedName("hasLocation")
    val hasLocation: Boolean
)