package com.example.learnkotlin.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()

            val response = chain.proceed(request)

            // HTTP lỗi do server trả
            when (response.code) {
                401 -> throw ApiException.Unauthorized("Token expired or invalid")
                403 -> throw ApiException.Forbidden("Access denied")
                404 -> throw ApiException.NotFound("Resource not found")
                in 500..599 -> throw ApiException.ServerError("Server error: ${response.code}", response.code)
            }

            response
        } catch (e: IOException) {
            // Lỗi network (timeout, no connection…)
            throw ApiException.NetworkError(e.message)
        } catch (e: ApiException) {
            // Rethrow ApiException đã có
            throw e
        } catch (e: Exception) {
            // Lỗi không xác định
            throw ApiException.UnknownError(e.message)
        }
    }
}