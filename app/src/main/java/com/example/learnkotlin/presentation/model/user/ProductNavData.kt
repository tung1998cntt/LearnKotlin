package com.example.learnkotlin.presentation.model.user

import com.example.learnkotlin.presentation.base.NavData
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductNavData(
    val id: String? = null,
    val name: String? = null,
    val price: String? = null
) : NavData