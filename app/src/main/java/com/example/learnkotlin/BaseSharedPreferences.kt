package com.example.learnkotlin

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class BaseSharedPreferences  constructor(prefName: String?) {

    companion object {
        @Volatile
        private var instance: BaseSharedPreferences? = null

        /**
         * Lấy instance BaseSharedPreferences mặc định
         */
        fun getInstance(prefName: String = "app_prefs"): BaseSharedPreferences {
            return instance ?: synchronized(this) {
                instance ?: BaseSharedPreferences(prefName).also { instance = it }
            }
        }

        /**
         * Hủy instance nếu cần (ví dụ logout, reset)
         */
        fun clearInstance() {
            instance = null
        }
    }

    private val prefs: SharedPreferences by lazy {
        MyApp.getContext().getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    // --- Các hàm tiện ích để lưu trữ / đọc ---

    fun putString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun getString(key: String, default: String? = null): String? {
        return prefs.getString(key, default)
    }

    fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    fun getInt(key: String, default: Int = 0): Int {
        return prefs.getInt(key, default)
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun putFloat(key: String, value: Float) {
        prefs.edit { putFloat(key, value) }
    }

    fun getFloat(key: String, default: Float = 0f): Float {
        return prefs.getFloat(key, default)
    }

    fun putLong(key: String, value: Long) {
        prefs.edit { putLong(key, value) }
    }

    fun getLong(key: String, default: Long = 0L): Long {
        return prefs.getLong(key, default)
    }

    fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    fun clearAll() {
        prefs.edit { clear() }
    }
}