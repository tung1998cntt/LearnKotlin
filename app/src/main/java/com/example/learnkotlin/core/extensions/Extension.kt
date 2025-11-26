package com.example.learnkotlin.core.extensions

import com.example.learnkotlin.core.network.ApiException
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.core.network.BaseResponse


suspend fun <T> safeApiCall(apiCall: suspend () -> BaseResponse<T>): ApiResult<T> {
    return try {
        val response = apiCall()
        if ((response.code ?: 0) in 200..299 && response.data != null) {
            ApiResult.Success(response.data)
        } else {
            ApiResult.Error(response.code.toString(), response.message ?: "Unknown error")
        }
    } catch (e: Exception) {
        // Chỉ còn Error, ApiException network / http / unknown đều map vào đây
        val code = if (e is ApiException) e.code else null
        val message = e.message ?: "Unknown error"
        ApiResult.Error(code.toString(), message)
    }
}