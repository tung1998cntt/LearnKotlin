package com.example.learnkotlin.presentation.model

import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductNavData(
    val id: String? = null,
    val name: String? = null,
    val price: String? = null
) : NavData