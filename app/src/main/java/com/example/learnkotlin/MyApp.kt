package com.example.learnkotlin

import android.app.Application
import android.content.Context
import android.content.res.Configuration

import dagger.hilt.android.HiltAndroidApp
//import org.koin.android.ext.koin.androidContext
//import org.koin.core.context.startKoin
import java.util.Locale

@HiltAndroidApp   /* Tạo singleton Component*/
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
//        startKoin {
//            androidContext(this@MyApp)
//            modules(appModule)
//        }
    }

    companion object {
        private var instance: MyApp? = null

        fun getContext(locale: Locale? = null): Context {
            val ctx = instance
                ?: throw IllegalStateException("Application chưa được khởi tạo")
            return if (locale == null) {
                ctx
            } else {
                val config = Configuration(ctx.resources.configuration)
                config.setLocale(locale)
                ctx.createConfigurationContext(config)
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }


}