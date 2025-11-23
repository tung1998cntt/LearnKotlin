package com.example.learnkotlin.presentation.base


sealed class NavigationCommand : UiCommand {
    data class Navigate(val navigationRoute: NavigationRoute?) : UiCommand

}