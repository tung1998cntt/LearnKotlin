package com.example.learnkotlin.presentation.home

import android.os.Parcelable
import com.example.learnkotlin.presentation.base.NavData
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuggestRouteItem(
    val routeId: String,
    val routeName: String,
    val busMinutes: Int,
    val walkMinutes: Int,
    val fromAddress: String? = null,
    val toAddress: String? = null,
): Parcelable, NavData