package com.example.learnkotlin.presentation.notification

import com.example.learnkotlin.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : BaseViewModel<NotificationState>() {
    override fun createInitialState() = NotificationState()

    override fun onReady() {
        super.onReady()
        loadNotifications()
    }

    private fun loadNotifications() {
        val fakeData = listOf(
            NotificationItem(
                "1",
                "Bus P1 delayed by 8 minutes",
                "Heavy traffic near Saramaccastraat. ETA updated.",
                "11:10:50",
                NotificationType.DELAY
            ),
            NotificationItem(
                "2",
                "Route W1 schedule update",
                "Last bus from Domburg now departs at 20:30",
                "09:25:22",
                NotificationType.SCHEDULE
            ),
            NotificationItem(
                "3",
                "Welcome to SuriBus! 👋",
                "Thank you for using SuriBus. Plan your journey on time with the app.",
                "20/06/2026 10:20:15",
                NotificationType.WELCOME
            )
        )
        updateState { copy(notifications = fakeData) }
    }
}
