package com.example.learnkotlin.domain.model.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteListRequest(
    val limit: Int,
    val offset: Int,
    val routeIds: List<String> = emptyList(),
    val status: List<String> = emptyList(),
    val keySearch: String? = null,
    val startTime: Long = 0L,
    val endTime: Long = 0L
) : Parcelable