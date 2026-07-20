package com.example.learnkotlin.presentation.home

import android.os.Parcelable
import com.example.learnkotlin.presentation.base.NavData
import kotlinx.parcelize.Parcelize

@Parcelize
data class NearbyArrivalItem(
    val routeId: String,
    val routeName: String,
    val plate: String? = null,
    val etaTime: String? = null,
    val etaMinutes: Int
) : Parcelable, NavData