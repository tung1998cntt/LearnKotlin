package com.example.learnkotlin.core.network

import com.example.learnkotlin.BuildConfig
import com.example.learnkotlin.data.model.response.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GeocodingApi {

    @GET("geocoding/{query}.json")
    suspend fun searchLocation(
        @Path("query") query: String? = null,
        @Query("key") key: String = BuildConfig.MAPTILER_API_KEY,
        @Query("language") language: String = "en"
    ): GeocodingResponseDto

}