package com.example.learnkotlin.presentation.route

import com.example.learnkotlin.domain.model.home.Area
import com.example.learnkotlin.domain.model.home.RouteDetail
import com.example.learnkotlin.domain.model.home.RouteDetailItem
import com.example.learnkotlin.domain.model.home.RouteItem
import com.example.learnkotlin.domain.model.home.SegmentType
import com.example.learnkotlin.domain.model.home.Variant
import com.example.learnkotlin.presentation.state.UiState

data class RouteState(

    val routes: List<RouteItem> = listOf(),
    val hasNext: Boolean = true,
    val loadingMore: Boolean = false,


    val searchKeyword: String = "",


    val areas: List<Area> = listOf(),
    val selectedArea: Area? = null,



    //========================
    // Route Detail
    //========================

    val routeDetail: RouteDetail? = null,

    val detailItems: List<RouteDetailItem> = emptyList(),

    val variant: Variant = Variant.OUTBOUND,

    val segment: SegmentType =
        SegmentType.ROUTE_INFORMATION

    ) : UiState