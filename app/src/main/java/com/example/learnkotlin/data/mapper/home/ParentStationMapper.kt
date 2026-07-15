package com.example.learnkotlin.data.mapper.home

import com.example.learnkotlin.data.model.response.ParentStationDto
import com.example.learnkotlin.domain.model.home.ParentStation

object ParentStationMapper {
    fun map(dto: ParentStationDto?): ParentStation? {

        dto ?: return null

        return ParentStation(
            gtfsId = dto.gtfsId,
            name = dto.name
        )
    }
}