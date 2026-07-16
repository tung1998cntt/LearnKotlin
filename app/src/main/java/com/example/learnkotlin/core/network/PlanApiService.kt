package com.example.learnkotlin.core.network

import com.example.learnkotlin.data.model.request.NearbyArrivalRequestDto
import com.example.learnkotlin.data.model.response.BusStopResponseDto
import com.example.learnkotlin.data.model.response.NearbyArrivalResponseDto
import com.example.learnkotlin.data.model.response.RoutePlanResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PlanApiService {

    @GET("api/v1/plan")
    suspend fun getSuggestRoutes(
        @Query("fromLat") fromLat: Double,
        @Query("fromLon") fromLon: Double,
        @Query("toLat") toLat: Double,
        @Query("toLon") toLon: Double
    ): RoutePlanResponseDto

    @POST("api/v1/nearby-arrivals")
    suspend fun getNearbyArrivals(
        @Body request: NearbyArrivalRequestDto
    ): NearbyArrivalResponseDto

    @GET("/api/v1/stops")
    suspend fun getStops(): BusStopResponseDto

}