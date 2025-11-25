package com.example.learnkotlin.presentation.base

import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.presentation.base.NavData


sealed class NavigationCommand: Command {
    data class ToActivity<T : NavData>(
        val clazz: Class<*>,
        val data: T? = null
    ) : NavigationCommand()
}