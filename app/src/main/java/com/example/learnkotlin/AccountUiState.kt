package com.example.learnkotlin

data class AccountUiState(
    val statusText: String? = null,
    val balance: Float = 0f,
    val transactions: List<String>? = null
)
