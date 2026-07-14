package com.example.learnkotlin.data.repository.home

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.example.learnkotlin.core.constants.Tags
import com.example.learnkotlin.core.extensions.safeApiCall
import com.example.learnkotlin.core.extensions.safeApiCallNotBase
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.core.network.ApiService
import com.example.learnkotlin.core.network.GeocodingApi
import com.example.learnkotlin.core.secure.SecureSharedPrefs
import com.example.learnkotlin.data.mapper.home.NearbyArrivalMapper
import com.example.learnkotlin.data.mapper.home.NearbyArrivalRequestMapper
import com.example.learnkotlin.data.mapper.home.RoutePlanMapper
import com.example.learnkotlin.data.mapper.home.SearchLocationMapper
import com.example.learnkotlin.data.model.request.LoginRequestDto
import com.example.learnkotlin.data.model.request.RouteListRequestDto
import com.example.learnkotlin.data.model.response.AreaDto
import com.example.learnkotlin.data.model.response.LoginResponseDto
import com.example.learnkotlin.data.model.response.RouteDetailResponseDto
import com.example.learnkotlin.data.model.response.RouteItemDto
import com.example.learnkotlin.data.model.response.RouteListResponseDto
import com.example.learnkotlin.data.model.response.RouteStopDto
import com.example.learnkotlin.domain.model.home.Area
import com.example.learnkotlin.domain.model.home.LoginRequest
import com.example.learnkotlin.domain.model.home.LoginResponse
import com.example.learnkotlin.domain.model.home.NearbyArrivalRequest
import com.example.learnkotlin.domain.model.home.NearbyArrivalResponse
import com.example.learnkotlin.domain.model.home.RouteDetail
import com.example.learnkotlin.domain.model.home.RouteItem
import com.example.learnkotlin.domain.model.home.RouteListRequest
import com.example.learnkotlin.domain.model.home.RouteListResponse
import com.example.learnkotlin.domain.model.home.RoutePlan
import com.example.learnkotlin.domain.model.home.RoutePlanRequest
import com.example.learnkotlin.domain.model.home.RoutePoint
import com.example.learnkotlin.domain.model.home.RouteStop
import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.domain.repository.home.HomeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class HomeRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val geocodingApi: GeocodingApi,
    private val apiService: ApiService,
    private val mapper: SearchLocationMapper,
    private val routePlanMapper: RoutePlanMapper,
    private val nearbyArrivalRequestMapper: NearbyArrivalRequestMapper,
    private val nearbyArrivalMapper: NearbyArrivalMapper

) : HomeRepository {
    private val geocoder by lazy {
        Geocoder(context, Locale.getDefault())
    }

    //    override suspend fun getUsers(): ApiResult<List<User>> {
//        delay(3000)
//        return safeApiCall { apiService.getUsers() }
//    }
//
//    override suspend fun addUser(name: String): ApiResult<List<User>> {
//        delay(3000)
//        return safeApiCall {
//            val list = mutableListOf<User>()
//            val user = User(id = 0 + 1, name = name)
//            list.add(user)
//            BaseResponse(
//                code = "200",
//                message = "OK",
//                data = list
//            )
//        }
//    }
    override suspend fun searchLocation(
        keyword: String
    ): List<SearchLocation> {
        return geocodingApi
            .searchLocation(query = keyword).features.map(mapper::map)
    }


    /* API vẫn hoạt động bình thường
     nhưng JetBrains chưa cam kết giữ nguyên
     tương lai có thể đổi tên, đổi tham số hoặc bị xóa.
     */

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun reverseLocation(
        latitude: Double,
        longitude: Double
    ): SearchLocation {
        val address = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    ) { addresses ->

                        if (continuation.isActive) {
                            continuation.resume(addresses.firstOrNull())
                        }
                    }
                }
            } else {
                geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                )?.firstOrNull()
            }
        } catch (e: Exception) {
            null
        }
        return address.toSearchLocation(
            latitude,
            longitude
        )
    }

    private fun Address?.toSearchLocation(
        latitude: Double,
        longitude: Double
    ): SearchLocation {

        val name =
            this?.getAddressLine(0)
                ?: this?.featureName
                ?: this?.thoroughfare
                ?: this?.subLocality
                ?: this?.locality
                ?: ""

        return SearchLocation(
            name = name,
            latitude = latitude,
            longitude = longitude
        )
    }

    override suspend fun getSuggestRoutes(
        request: RoutePlanRequest
    ): RoutePlan {

        return apiService.getSuggestRoutes(
            fromLat = request.fromLat,
            fromLon = request.fromLon,
            toLat = request.toLat,
            toLon = request.toLon
        ).let(routePlanMapper::map)
    }

    override suspend fun getNearbyArrivals(
        request: NearbyArrivalRequest
    ): NearbyArrivalResponse {

        return apiService.getNearbyArrivals(
            nearbyArrivalRequestMapper.map(request)
        ).let(nearbyArrivalMapper::map)
    }

    override suspend fun getRouteList(
        request: RouteListRequest
    ): ApiResult<RouteListResponse> {

        val body = RouteListRequestDto(
            routeIds = request.routeIds,
            status = request.status,
            search = request.keySearch,
        )

        val result = safeApiCallNotBase {
            apiService.getRouteList(
                limit = request.limit,
                offset = request.offset,
                body = body
            )
        }

        return when (result) {
            is ApiResult.Success ->
                ApiResult.Success(result.data.toDomain())

            is ApiResult.Error ->
                result
        }
    }

    fun RouteListResponseDto.toDomain() =
        RouteListResponse(
            total = total,
            offset = offset,
            limit = limit,
            routes = routes.map { it.toDomain() }
        )

    private fun RouteItemDto.toDomain() =
        RouteItem(
            id = id,
            routeName = routeName,
            orgId = orgId,
            orgName = orgName,
            outboundDistance = outboundDistance,
            inboundDistance = inboundDistance,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
            outboundStops = outboundStops,
            inboundStops = inboundStops,
            udPermission = udPermission,
            auxiliaryImei = auxiliaryImei,
            delayTime = delayTime
        )

    override suspend fun getAreaList(): ApiResult<List<Area>> {

        val result = safeApiCallNotBase {
            apiService.getAreaList()
        }

        return when (result) {
            is ApiResult.Success ->
                ApiResult.Success(
                    result.data.map { it.toDomain() }
                )

            is ApiResult.Error ->
                result
        }
    }

    fun AreaDto.toDomain() =
        Area(
            id = id,
            name = name
        )


    override suspend fun login(
        request: LoginRequest
    ): ApiResult<LoginResponse> {

        val body = LoginRequestDto(
            username = request.username,
            password = request.password
        )

        return when (val result = safeApiCallNotBase {
            apiService.login(body)
        }) {

            is ApiResult.Success -> {
                val loginResponse = result.data.toDomain()
                // Cache token
                SecureSharedPrefs.get().putString(Tags.ACCESS_TOKEN, loginResponse.token)
                ApiResult.Success(loginResponse)
            }

            is ApiResult.Error -> result
        }
    }

    fun LoginResponseDto.toDomain() =
        LoginResponse(
            token = token,
            expiredAt = expiredAt,
            deviceToken = deviceToken,
            isAdmin = isAdmin,
            userId = userId,
            roleId = roleId,
            roleName = roleName,
            roleType = roleType,
            projectId = projectId,
            orgId = orgId,
            name = name,
            phone = phone,
            username = username
        )

    override suspend fun getRouteDetail(
        id: String,
        variant: String
    ): ApiResult<RouteDetail> {
        return when (
            val result = safeApiCallNotBase {
                apiService.getRouteDetail(
                    id,
                    variant
                )
            }
        ) {
            is ApiResult.Success ->
                ApiResult.Success(
                    result.data.toDomain()
                )
            is ApiResult.Error -> result
        }
    }


    fun RouteDetailResponseDto.toDomain() =
        RouteDetail(

            id = id,

            routeName = routeName,

            orgId = orgId,

            orgName = orgName,

            outboundDistance = outboundDistance,

            inboundDistance = inboundDistance,

            status = status,

            createdAt = createdAt,

            updatedAt = updatedAt,

            outboundStops = outboundStops?.map {
                it.toDomain()
            },

            inboundStops = inboundStops?.map {
                it.toDomain()
            } ?: listOf() ,

            udPermission = udPermission,

            auxiliaryImei = auxiliaryImei,

            delayTime = delayTime
        )

    fun RouteStopDto.toDomain() =
        RouteStop(

            id = id,

            stopName = stopName,

            latitude = latitude,

            longitude = longitude,

            notificationCode = notificationCode,

            stopOrder = stopOrder,

            pathPoints = pathPoints?.map {

                RoutePoint(

                    longitude = it[0],

                    latitude = it[1]

                )
            } ?: listOf()
        )

}