package com.example.learnkotlin.presentation.base

import com.example.learnkotlin.domain.base.Command

data class InitDataCommand<T : Any?>(val data: T?) : Command