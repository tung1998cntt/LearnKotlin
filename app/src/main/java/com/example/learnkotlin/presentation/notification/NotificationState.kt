package com.example.learnkotlin.presentation.notification

import com.example.learnkotlin.presentation.state.UiState

data class NotificationState(
    val notifications: List<NotificationItem> = emptyList()
) : UiState
