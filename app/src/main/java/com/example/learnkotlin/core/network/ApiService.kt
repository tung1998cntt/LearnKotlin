package com.example.learnkotlin.core.network

import com.example.learnkotlin.data.model.request.LoginRequestDto
import com.example.learnkotlin.data.model.request.NearbyArrivalRequestDto
import com.example.learnkotlin.data.model.request.RouteListRequestDto
import com.example.learnkotlin.data.model.response.AreaDto
import com.example.learnkotlin.data.model.response.BusStopResponseDto
import com.example.learnkotlin.data.model.response.LoginResponseDto
import com.example.learnkotlin.data.model.response.NearbyArrivalResponseDto
import com.example.learnkotlin.data.model.response.RouteDetailResponseDto
import com.example.learnkotlin.data.model.response.RouteListResponseDto
import com.example.learnkotlin.data.model.response.RoutePlanResponseDto
import com.example.learnkotlin.domain.model.user.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    suspend fun getUsers(): BaseResponse<List<User>>

    @POST("/api/devices/vtracking/vehicle/route/list")
    suspend fun getRouteList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Body body: RouteListRequestDto
    ): RouteListResponseDto


    @GET("/api/v1/areas")
    suspend fun getAreaList(): List<AreaDto>

    @POST("/api/app/vtracking/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): LoginResponseDto


    @GET("/api/devices/vtracking/vehicle/route/{id}")
    suspend fun getRouteDetail(
        @Path("id")
        id:String,
        @Query("variant")
        variant:String
    ): RouteDetailResponseDto


}