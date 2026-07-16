package com.example.learnkotlin.presentation.home

import com.example.learnkotlin.domain.model.home.BusStop
import com.example.learnkotlin.domain.model.home.RouteDetail
import com.example.learnkotlin.domain.model.home.RouteDetailItem
import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.domain.model.home.SegmentType
import com.example.learnkotlin.domain.model.home.Variant
import com.example.learnkotlin.presentation.state.UiState

data class HomeState(
    val currentKeyword: String = "",
    val selectedCurrentLocation: SearchLocation? = null,

    val destinationKeyword: String = "",
    val selectedDestination: SearchLocation? = null,

    //========================
    // Bus Stops
    //========================
    val busStops: List<BusStop> = emptyList(),
    // Thêm trường này để lookup nhanh
    val busStopsMap: Map<String, BusStop> = emptyMap(),

    //========================
    // Nearby Arrivals
    //========================
    val listNearbyArrivalItem: List<NearbyArrivalItem> = emptyList(),
    val nearbyArrivalPage: Int = 1,
    val isNearbyArrivalLastPage: Boolean = false,
    val isLoadingMoreNearby: Boolean = false,

    //========================
    // Route Detail
    //========================

    val routeDetail: RouteDetail? = null,

    val detailItems: List<RouteDetailItem> = emptyList(),

    val variant: Variant = Variant.OUTBOUND,

    val segment: SegmentType =
        SegmentType.ROUTE_INFORMATION



) : UiState