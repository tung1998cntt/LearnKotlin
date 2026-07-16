package com.example.learnkotlin.presentation.home

import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.model.home.Area
import com.example.learnkotlin.domain.model.home.BusStopRoute
import com.example.learnkotlin.domain.model.home.LocationSearch
import com.example.learnkotlin.domain.model.home.NearbyArrivalResponse
import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.domain.model.home.SegmentType
import com.example.learnkotlin.domain.model.home.Variant

sealed class HomeCommand : Command {

    data class SearchKeyLocation(
        val keyword: String
    ) : HomeCommand()

    data class SelectCurrentLocation(
        val keyword: String = "",
        val location: SearchLocation? = null
    ) : HomeCommand()

    data object ClearCurrentLocation : HomeCommand()

    data class SearchKeyDestination(
        val keyword: String
    ) : HomeCommand()

    data class SelectDestination(
        val keyword: String = "",
        val location: SearchLocation? = null
    ) : HomeCommand()

    data object ClearDestination : HomeCommand()

    data class ReverseLocation(
        val latitude: Double,
        val longitude: Double,
        val field: SelectedField
    ) : HomeCommand()

    object FindRoute : HomeCommand()

    object GetRoute : HomeCommand()
    data object LoadMore : HomeCommand()


    data object GetArea : HomeCommand()
    data class SelectArea(
        val area: Area
    ) : HomeCommand()

    data class SearchRoute(val keyword: String) : HomeCommand()

    data class GetRouteDetail(
        val routeId: String? = null,
        val variant: String = "outbound"
    ) : HomeCommand()

    data class ChangeVariant(
        val variant: Variant
    ) : HomeCommand()

    data class ChangeSegment(
        val segment: SegmentType
    ) : HomeCommand()

    data class GetSuggestRoutes(val locationSearch: LocationSearch? = null) : HomeCommand()

    data class GetNearbyRoutes(val locationSearch: LocationSearch? = null) : HomeCommand()


}