package com.example.learnkotlin.data.mapper.home

import com.example.learnkotlin.data.mapper.Mapper
import com.example.learnkotlin.data.model.response.ItineraryDto
import com.example.learnkotlin.data.model.response.LegDto
import com.example.learnkotlin.data.model.response.LocationDto
import com.example.learnkotlin.data.model.response.RoutePlanResponseDto
import com.example.learnkotlin.domain.model.home.Itinerary
import com.example.learnkotlin.domain.model.home.RouteLeg
import com.example.learnkotlin.domain.model.home.RouteLocation
import com.example.learnkotlin.domain.model.home.RoutePlan
import javax.inject.Inject

class RoutePlanMapper @Inject constructor() :
    Mapper<RoutePlanResponseDto, RoutePlan> {

    override fun map(input: RoutePlanResponseDto): RoutePlan {

        return RoutePlan(
            requestId = input.requestId.orEmpty(),
            from = input.from.toDomain(),
            to = input.to.toDomain(),
            itineraries = input.itineraries
                ?.map { it.toDomain() }
                .orEmpty()
        )
    }

    private fun LocationDto?.toDomain(): RouteLocation {

        return RouteLocation(
            name = this?.name.orEmpty(),
            latitude = this?.lat ?: 0.0,
            longitude = this?.lon ?: 0.0
        )
    }

    private fun ItineraryDto.toDomain() =
        Itinerary(

            startTime = startTime ?: 0,

            endTime = endTime ?: 0,

            duration = duration ?: 0,

            waitingTime = waitingTime ?: 0,

            distance = distance ?: 0.0,

            transfers = transfers ?: 0,

            legs = legs
                ?.map { it.toDomain() }
                .orEmpty()
        )

    private fun LegDto.toDomain() =
        RouteLeg(

            transitLeg = transitLeg ?: false,

            startTime = startTime ?: 0,

            endTime = endTime ?: 0,

            mode = mode.orEmpty(),

            duration = duration ?: 0,

            routeName = routeName.orEmpty(),

            routeId = routeId.orEmpty(),

            headsign = headsign.orEmpty(),

            from = from.toDomain(),

            to = to.toDomain(),

            distance = distance ?: 0.0,

            agencyId = agencyId.orEmpty(),

            agencyName = agencyName.orEmpty()
        )

}