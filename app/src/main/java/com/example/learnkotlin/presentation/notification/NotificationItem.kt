package com.example.learnkotlin.presentation.notification

import android.os.Parcelable
import com.example.learnkotlin.presentation.base.NavData
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationItem(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val time: String? = null,
    val type: NotificationType = NotificationType.DELAY
) : Parcelable, NavData

enum class NotificationType {
    DELAY, SCHEDULE, WELCOME
}
