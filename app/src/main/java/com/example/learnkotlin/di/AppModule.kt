package com.example.learnkotlin.di

import com.example.learnkotlin.data.repository.UserRepositoryImpl
import com.example.learnkotlin.domain.repository.UserRepository
import com.example.learnkotlin.domain.usecase.UserUseCase
import com.example.learnkotlin.presentation.ui.user_management.UserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    factory { UserUseCase(get()) }
    viewModel { UserViewModel() }
}