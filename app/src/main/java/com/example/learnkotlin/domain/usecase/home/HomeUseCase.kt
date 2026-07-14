package com.example.learnkotlin.domain.usecase.home

import com.example.learnkotlin.core.network.ApiResult
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
import com.example.learnkotlin.domain.repository.home.HomeRepository
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val homeRepository: HomeRepository

) {

    private val collator = Collator.getInstance(Locale("vi", "VN")).apply {
        strength = Collator.PRIMARY
    }


//    suspend fun loadUsers(): ApiResult<List<User>> {
//        return homeRepository.getUsers()
//    }
//    suspend fun addUser(name: String): ApiResult<List<User>> = homeRepository.addUser(name)

    suspend fun searchLocation(keyword: String): List<SearchLocation> {
        return homeRepository.searchLocation(keyword)
    }

    suspend fun reverseLocation(
        latitude: Double,
        longitude: Double
    ): SearchLocation {

        return homeRepository.reverseLocation(
            latitude,
            longitude
        )
    }

    suspend fun getSuggestRoutes(
        request: RoutePlanRequest
    ): RoutePlan {
        return homeRepository.getSuggestRoutes(request)
    }

    suspend fun getNearbyArrivals(
        request: NearbyArrivalRequest
    ): NearbyArrivalResponse {

        return homeRepository.getNearbyArrivals(request)
    }
    suspend fun getRouteList(
        request: RouteListRequest
    ): ApiResult<RouteListResponse> {

        return when (val result = homeRepository.getRouteList(request)) {

            is ApiResult.Success -> {
                ApiResult.Success(
                    result.data.copy(
                        routes = result.data.routes.sortedWith { a, b ->
                            collator.compare(
                                a.routeName,
                                b.routeName
                            )
                        }
                    )
                )
            }

            is ApiResult.Error -> result
        }
    }

    suspend fun getAreaList() =
        homeRepository.getAreaList()

    suspend fun login(
        request: LoginRequest
    ): ApiResult<LoginResponse> {
        return homeRepository.login(request)
    }

    suspend fun getRouteDetail(
        id: String,
        variant: String
    ) = homeRepository.getRouteDetail(id, variant)

}