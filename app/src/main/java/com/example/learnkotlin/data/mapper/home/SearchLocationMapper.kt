package com.example.learnkotlin.data.mapper.home

import com.example.learnkotlin.data.mapper.Mapper
import com.example.learnkotlin.data.model.response.FeatureDto
import com.example.learnkotlin.domain.model.home.SearchLocation
import javax.inject.Inject

class SearchLocationMapper @Inject constructor() :
    Mapper<FeatureDto, SearchLocation> {

    override fun map(
        input: FeatureDto
    ): SearchLocation {

        return SearchLocation(
            name = input.placeName,
            latitude = input.center?.getOrNull(1) ?: 0.0,
            longitude = input.center?.getOrNull(0) ?: 0.0
        )
    }
}