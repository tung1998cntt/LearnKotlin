package com.example.learnkotlin.data.mapper.home

import com.example.learnkotlin.data.model.response.RouteDto
import com.example.learnkotlin.domain.model.home.BusStopRoute
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object BusStopRouteMapper {

    private val mockStartTime = LocalTime.of(10, 57)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun map(dto: RouteDto, stopIndex: Int): BusStopRoute {
        val vehicle = dto.vehicles?.firstOrNull()

        return BusStopRoute(
            gtfsId = dto.gtfsId,
            shortName = dto.shortName,
            description = dto.desc,
            mode = dto.mode,
            color = dto.color,
            textColor = dto.textColor,
            routeId = dto.routeId,
            licensePlate = vehicle?.licensePlate
                ?.takeIf { it.isNotBlank() }
                ?: "PA-${1200 + stopIndex}",
            etaTime = vehicle?.etaTime.toHourMinute().takeIf { it.isNotBlank() }
                ?: mockStartTime
                    .plusMinutes(stopIndex * 5L)
                    .format(timeFormatter)
        )
    }

    private fun String?.toHourMinute(): String {

        if (this.isNullOrBlank()) return ""

        return runCatching {
            java.time.OffsetDateTime.parse(this)
                .toLocalTime()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
        }.getOrDefault("")
    }
}