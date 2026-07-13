package com.example.learnkotlin.data.mapper.home

import com.example.learnkotlin.data.mapper.Mapper
import com.example.learnkotlin.data.model.request.NearbyArrivalRequestDto
import com.example.learnkotlin.domain.model.home.NearbyArrivalRequest
import javax.inject.Inject

class NearbyArrivalRequestMapper @Inject constructor() :
    Mapper<NearbyArrivalRequest, NearbyArrivalRequestDto> {

    override fun map(input: NearbyArrivalRequest): NearbyArrivalRequestDto {

        return NearbyArrivalRequestDto(
            lat = input.lat,
            lon = input.lon,
            radiusMeters = input.radiusMeters,
            limit = input.limit
        )
    }
}