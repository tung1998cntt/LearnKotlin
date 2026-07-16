package com.example.learnkotlin.domain.model.home

data class NearbyArrivalRequest(
    val lat: Double,
    val lon: Double,
    val radiusMeters: Int,
    val limit: Int,
    val time: String? = null,
    val page: Int? = null
)