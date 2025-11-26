package com.example.learnkotlin.core.network

sealed class ApiResult<out T> {
    data class Success<T>(val data: T): ApiResult<T>()
    data class Error(val code: String, val message: String?): ApiResult<Nothing>()
}