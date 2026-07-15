package com.example.learnkotlin.data.mapper.home

import com.example.learnkotlin.data.model.response.RouteDto
import com.example.learnkotlin.domain.model.home.BusStopRoute

object BusStopRouteMapper  {

    fun map(dto: RouteDto): BusStopRoute {

        return BusStopRoute(
            gtfsId = dto.gtfsId,
            shortName = dto.shortName,
            description = dto.desc,
            mode = dto.mode,
            color = dto.color,
            textColor = dto.textColor,
            routeId = dto.routeId
        )
    }
}