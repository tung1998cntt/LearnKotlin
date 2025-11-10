package com.example.learnkotlin

import java.util.Locale

class AppData private constructor() {

    private val prefs: BaseSharedPreferences by lazy { BaseSharedPreferences("app_data_prefs") }

    var language: AppLanguage
        get() {
            val code = prefs.getString("key_language", AppLanguage.VIETNAMESE.code)
            return if (code.isNullOrEmpty()) AppLanguage.ENGLISH else AppLanguage.fromCode(code)
        }
        set(value) {
            prefs.putString("key_language", value.code)
        }

    fun getLocale(): Locale = Locale(language.code)

    companion object {
        @Volatile
        private var instance: AppData? = null

        fun getInstance(): AppData {
            return instance ?: synchronized(this) {
                instance ?: AppData().also { instance = it }
            }
        }

        fun clearInstance() {
            instance = null
        }
    }
}