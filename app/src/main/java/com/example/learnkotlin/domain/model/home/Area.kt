package com.example.learnkotlin.domain.model.home

data class Area(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)