package com.example.learnkotlin.di

import com.example.learnkotlin.data.repository.user.UserRepositoryImpl
import com.example.learnkotlin.domain.repository.user.UserRepository
import com.example.learnkotlin.domain.usecase.user.UserUseCase
import com.example.learnkotlin.presentation.ui.user.UserDetailViewModel
import com.example.learnkotlin.presentation.ui.user.UserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    //single<UserRepository> { UserRepositoryImpl() }
    factory<UserRepository> { UserRepositoryImpl() }
    factory { UserUseCase(get()) }
    viewModel { UserViewModel() }
    viewModel { UserDetailViewModel() }
}