package com.example.learnkotlin.core.secure

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.learnkotlin.MyApp

class SecureSharedPrefs private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun putString(key: String, value: String?) {
        if (value == null) {
            prefs.edit { remove(key) }
            return
        }
        val encrypted = EncryptHelper.encrypt(value)
        prefs.edit { putString(key, encrypted) }
    }

    fun getString(key: String, def: String? = null): String? {
        val encrypted = prefs.getString(key, null) ?: return def
        return try {
            EncryptHelper.decrypt(encrypted)
        } catch (e: Exception) {
            def
        }
    }

    fun putBoolean(key: String, value: Boolean) =
        putString(key, value.toString())

    fun getBoolean(key: String, def: Boolean = false): Boolean =
        getString(key)?.toBoolean() ?: def

    fun putInt(key: String, value: Int) =
        putString(key, value.toString())

    fun getInt(key: String, def: Int = 0): Int =
        getString(key)?.toIntOrNull() ?: def

    fun putLong(key: String, value: Long) =
        putString(key, value.toString())

    fun getLong(key: String, def: Long = 0): Long =
        getString(key)?.toLongOrNull() ?: def

    fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        private const val PREF_NAME = "secure_prefs"

        @Volatile private var instance: SecureSharedPrefs? = null

        fun get(): SecureSharedPrefs {
            return instance ?: synchronized(this) {
                instance ?: SecureSharedPrefs(MyApp.Companion.getContext()).also { instance = it }
            }
        }
    }
}