package com.example.learnkotlin.presentation.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class NavigationRoute : Parcelable {
    @Parcelize
    data class ToActivity(val clazz: Class<*>, val data: Parcelable?) : NavigationRoute()

    @Parcelize
    data class ToFragment(
        val fragmentClass: Class<*>,
        val data: Parcelable?,
        val containerId: Int,
        val addToBackStack: Boolean = true
    ) : NavigationRoute()
}

