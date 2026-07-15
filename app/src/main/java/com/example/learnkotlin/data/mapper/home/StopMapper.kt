package com.example.learnkotlin.data.mapper.home

import com.example.learnkotlin.data.model.response.BusStopDto
import com.example.learnkotlin.domain.model.home.BusStop

object StopMapper {

    fun map(dto: BusStopDto): BusStop {

        return BusStop(
            gtfsId = dto.gtfsId,
            name = dto.name,
            code = dto.code,
            description = dto.desc,
            latitude = dto.lat,
            longitude = dto.lon,
            locationType = dto.locationType,
            platformCode = dto.platformCode,
            zoneId = dto.zoneId,
            vehicleMode = dto.vehicleMode,
            wheelchairBoarding = dto.wheelchairBoarding,
            parentStation = ParentStationMapper.map(dto.parentStation),
            routes = dto.routes?.map(BusStopRouteMapper::map)
        )
    }

    fun map(list: List<BusStopDto>): List<BusStop> {
        return list.map(::map)
    }
}