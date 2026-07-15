package com.example.learnkotlin.data.mapper.home

import com.example.learnkotlin.data.mapper.Mapper
import com.example.learnkotlin.data.model.response.ArrivalDto
import com.example.learnkotlin.data.model.response.NearbyArrivalResponseDto
import com.example.learnkotlin.data.model.response.OriginDto
import com.example.learnkotlin.data.model.response.PaginationDto
import com.example.learnkotlin.data.model.response.StopDto
import com.example.learnkotlin.data.model.response.VehicleDto
import com.example.learnkotlin.domain.model.home.Arrival
import com.example.learnkotlin.domain.model.home.NearbyArrivalResponse
import com.example.learnkotlin.domain.model.home.Origin
import com.example.learnkotlin.domain.model.home.Pagination
import com.example.learnkotlin.domain.model.home.Stop
import com.example.learnkotlin.domain.model.home.Vehicle
import com.example.learnkotlin.presentation.home.NearbyArrivalItem
import javax.inject.Inject

class NearbyArrivalMapper @Inject constructor() :
    Mapper<NearbyArrivalResponseDto, NearbyArrivalResponse> {

    override fun map(input: NearbyArrivalResponseDto): NearbyArrivalResponse {

        return NearbyArrivalResponse(
            origin = input.origin?.toDomain(),
            radiusMeters = input.radiusMeters,
            pagination = input.pagination?.toDomain(),
            stops = input.stops.map { it.toDomain() },
            listNearbyArrivalItem = input.toItems()
        )
    }

    fun NearbyArrivalResponseDto.toItems(): List<NearbyArrivalItem> {

        return stops.flatMap { stop ->

            stop.arrivals.map { arrival ->

                val vehicle = arrival.vehicles.firstOrNull()

                NearbyArrivalItem(
                    routeId = arrival.routeId.substringAfter(":"),
                    routeName = stop.stopName,
                    plate = vehicle?.licensePlate.orEmpty(),
                    etaTime = vehicle?.etaTime?.toHourMinute() ?: "",       // 19:10
                    etaMinutes = arrival.minutesToArrival
                )
            }
        }
    }

    private fun String?.toHourMinute(): String {

        if (this.isNullOrBlank()) return ""

        return runCatching {
            java.time.OffsetDateTime.parse(this)
                .toLocalTime()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
        }.getOrDefault("")
    }
    private fun OriginDto.toDomain() = Origin(
        lat = lat,
        lon = lon
    )

    private fun PaginationDto.toDomain() = Pagination(
        page = page,
        pageSize = pageSize,
        returnedItems = returnedItems,
        hasNext = hasNext
    )

    private fun StopDto.toDomain() = Stop(
        stopId = stopId,
        stopName = stopName,
        lat = lat,
        lon = lon,
        distanceMeters = distanceMeters,
        arrivals = arrivals.map { it.toDomain() }
    )

    private fun ArrivalDto.toDomain() = Arrival(
        routeId = routeId.substringAfter(":"),
        routeShortName = routeShortName,
        routeLongName = routeLongName,
        tripId = tripId,
        headsign = headsign,
        scheduledArrival = scheduledArrival,
        estimatedArrival = estimatedArrival,
        delaySeconds = delaySeconds,
        minutesToArrival = minutesToArrival,
        realtime = realtime,
        vehicles = vehicles.map { it.toDomain() }
    )

    private fun VehicleDto.toDomain() = Vehicle(
        vehicleId = vehicleId,
        licensePlate = licensePlate,
        lat = lat,
        lon = lon,
        heading = heading,
        speedKmh = speedKmh,
        distanceKm = distanceKm,
        distanceSource = distanceSource,
        etaSeconds = etaSeconds,
        etaTime = etaTime,
        walkDistanceKm = walkDistanceKm,
        walkEtaSeconds = walkEtaSeconds,
        walkEtaTime = walkEtaTime,
        walkSource = walkSource,
        hasLocation = hasLocation
    )
}