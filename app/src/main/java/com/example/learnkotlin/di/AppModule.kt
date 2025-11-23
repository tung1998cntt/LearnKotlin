package com.example.learnkotlin.di

import com.example.learnkotlin.presentation.ui.user_management.UserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    viewModel { UserViewModel() }
}