package com.example.learnkotlin.data.mapper.home

import com.example.learnkotlin.data.mapper.Mapper
import com.example.learnkotlin.data.model.request.RoutePlanRequestDto
import com.example.learnkotlin.domain.model.home.RoutePlanRequest
import javax.inject.Inject

class RoutePlanRequestMapper @Inject constructor() :
    Mapper<RoutePlanRequest, RoutePlanRequestDto> {

    override fun map(input: RoutePlanRequest): RoutePlanRequestDto {

        return RoutePlanRequestDto(
            fromLat = input.fromLat,
            fromLon = input.fromLon,
            toLat = input.toLat,
            toLon = input.toLon
        )
    }
}