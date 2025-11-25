package com.example.learnkotlin.data.model

enum class AppLanguage(val code: String) {
    VIETNAMESE("vi"),
    ENGLISH("en");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.firstOrNull { it.code == code } ?: VIETNAMESE
        }
    }
}