package com.example.learnkotlin.domain.model.home

import android.os.Parcelable
import com.example.learnkotlin.presentation.base.NavData
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchLocation(
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
): Parcelable, NavData