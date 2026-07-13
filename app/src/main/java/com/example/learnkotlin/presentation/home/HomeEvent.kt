package com.example.learnkotlin.presentation.home

import com.example.learnkotlin.domain.base.Event
import com.example.learnkotlin.domain.model.home.RouteItem
import com.example.learnkotlin.domain.model.home.RouteListResponse
import com.example.learnkotlin.domain.model.home.SearchLocation

sealed class HomeEvent : Event {
    data class SearchLocationSuccess(
        val data: List<SearchLocation>? = null
    ) : HomeEvent()

    data class ShowError(
        val message: String? = null
    ) : HomeEvent()

    data class SearchDestinationSuccess(
        val data: List<SearchLocation>? = null
    ) : HomeEvent()

    data class GetRouteSuccess(
        val listRoute: List<RouteItem>? = null
    ) : HomeEvent()
}