package com.example.learnkotlin.data

import com.example.learnkotlin.data.model.AppLanguage
import com.example.learnkotlin.core.secure.SecureSharedPrefs
import java.util.Locale

class AppData private constructor() {

    private val prefs by lazy { SecureSharedPrefs.get() }

    var language: AppLanguage
        get() {
            val code = prefs.getString("key_language", AppLanguage.VIETNAMESE.code)
            return AppLanguage.Companion.fromCode(code)
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