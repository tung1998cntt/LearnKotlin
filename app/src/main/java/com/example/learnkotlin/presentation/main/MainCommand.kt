package com.example.learnkotlin.presentation.main

import com.example.learnkotlin.domain.base.Command

class MainCommand : Command {
    object LoadHomeCommand : Command

    object LoadProfileCommand : Command

    object LoadRouteCommand : Command
}