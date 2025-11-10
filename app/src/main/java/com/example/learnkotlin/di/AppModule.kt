package com.example.learnkotlin.di

import com.example.learnkotlin.MyRepository
import com.example.learnkotlin.MyViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single { MyRepository() }
    viewModel { MyViewModel(get()) }
}