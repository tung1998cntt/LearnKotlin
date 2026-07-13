package com.example.learnkotlin.domain.repository.home

import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.domain.model.home.Area
import com.example.learnkotlin.domain.model.home.LoginRequest
import com.example.learnkotlin.domain.model.home.LoginResponse
import com.example.learnkotlin.domain.model.home.NearbyArrivalRequest
import com.example.learnkotlin.domain.model.home.NearbyArrivalResponse
import com.example.learnkotlin.domain.model.home.RouteListRequest
import com.example.learnkotlin.domain.model.home.RouteListResponse
import com.example.learnkotlin.domain.model.home.RoutePlan
import com.example.learnkotlin.domain.model.home.RoutePlanRequest
import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.domain.model.user.User

interface HomeRepository {
    //    suspend fun getUsers(): ApiResult<List<User>>
//    suspend fun addUser(name: String): ApiResult<List<User>>
    suspend fun searchLocation(
        keyword: String
    ): List<SearchLocation>

    suspend fun reverseLocation(
        latitude: Double,
        longitude: Double
    ): SearchLocation

    suspend fun getSuggestRoutes(
        request: RoutePlanRequest
    ): RoutePlan

    suspend fun getNearbyArrivals(
        request: NearbyArrivalRequest
    ): NearbyArrivalResponse

    suspend fun getRouteList(
        request: RouteListRequest
    ): ApiResult<RouteListResponse>

    suspend fun getAreaList(): ApiResult<List<Area>>

    suspend fun login(
        request: LoginRequest
    ): ApiResult<LoginResponse>

}