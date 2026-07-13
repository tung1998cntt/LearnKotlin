package com.example.learnkotlin.domain.model.home

data class RoutePlanRequest(
    val fromLat: Double,
    val fromLon: Double,
    val toLat: Double,
    val toLon: Double
)
