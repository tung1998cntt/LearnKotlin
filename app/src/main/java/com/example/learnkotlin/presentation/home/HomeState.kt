package com.example.learnkotlin.presentation.home

import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.presentation.state.UiState

data class HomeState(
    val currentKeyword: String = "",
    val selectedCurrentLocation: SearchLocation? = null,

    val destinationKeyword: String = "",
    val selectedDestination: SearchLocation? = null,
) : UiState