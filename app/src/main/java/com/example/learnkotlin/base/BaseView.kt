package com.example.learnkotlin.base

import android.content.Context

interface BaseView {
    fun showLoading(isLoading: Boolean)
    fun showError(message: String?)
    fun getContext(): Context
}