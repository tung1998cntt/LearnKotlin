package com.example.learnkotlin.core.logger

import android.util.Log
object BaseLog {

    // Bật log trong debug, tắt trong release
    val ENABLE_LOG: Boolean = BuildConfig.DEBUG
    const val DEFAULT_TAG = "APP_LOG"

    /** Debug log */
    fun d(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (ENABLE_LOG) {
            if (throwable != null) Log.d(tag, message, throwable)
            else Log.d(tag, message)
        }
    }

    /** Info log */
    fun i(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (ENABLE_LOG) {
            if (throwable != null) Log.i(tag, message, throwable)
            else Log.i(tag, message)
        }
    }

    /** Warning log */
    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (ENABLE_LOG) {
            if (throwable != null) Log.w(tag, message, throwable)
            else Log.w(tag, message)
        }
    }

    /** Error log */
    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (ENABLE_LOG) {
            if (throwable != null) Log.e(tag, message, throwable)
            else Log.e(tag, message)
        }
    }

    /** Helper in-line log */
    inline fun logD(tag: String = DEFAULT_TAG, crossinline message: () -> String) {
        if (ENABLE_LOG) Log.d(tag, message())
    }

    inline fun logI(tag: String = DEFAULT_TAG, crossinline message: () -> String) {
        if (ENABLE_LOG) Log.i(tag, message())
    }

    inline fun logW(tag: String = DEFAULT_TAG, crossinline message: () -> String) {
        if (ENABLE_LOG) Log.w(tag, message())
    }

    inline fun logE(tag: String = DEFAULT_TAG, crossinline message: () -> String) {
        if (ENABLE_LOG) Log.e(tag, message())
    }
}

object BuildConfig {
    const val DEBUG = true

    // Các field tuỳ chỉnh như API URL, keys, flags...
    const val API_URL = "https://api.example.com"
    const val API_KEY = "your-api-key"
}