package com.example.learnkotlin.domain.model.home

import com.example.learnkotlin.presentation.base.NavData
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationSearch(
    val selectedCurrentLocation: SearchLocation? = null,
    val selectedDestination: SearchLocation? = null,
): NavData