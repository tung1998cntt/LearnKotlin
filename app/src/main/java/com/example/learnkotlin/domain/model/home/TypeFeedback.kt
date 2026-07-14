package com.example.learnkotlin.domain.model.home

data class TypeFeedback(
    var name: String,
    var isSelected: Boolean = false,
    var isEnabled: Boolean = true,
    val hours: Int = 0
)
