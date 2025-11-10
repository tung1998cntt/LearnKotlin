package com.example.learnkotlin.di

import com.example.learnkotlin.MyRepository
import com.example.learnkotlin.MyViewModel
import com.example.learnkotlin.data.remote.ApiService
import com.example.learnkotlin.data.repository.DataRepository
import com.example.learnkotlin.ui.MainContract
import com.example.learnkotlin.ui.MainPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
/*
factory — Tạo mới mỗi lần inject
Phù hợp cho class có vòng đời ngắn, ví dụ:
Presenter trong MVP (1 Activity/Fragment gắn 1 Presenter)
Adapter RecyclerView
DialogController
*
*
* */
    single { androidContext().applicationContext }
    single { MyRepository() }
    single { DataRepository(get()) }
    single { ApiService() }
    factory<MainContract.Presenter> { MainPresenter(get()) }
    viewModel { MyViewModel(get()) }
}