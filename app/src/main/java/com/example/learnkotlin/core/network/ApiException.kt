package com.example.learnkotlin.core.network

sealed class ApiException(message: String? = null, val code: Int? = null) : Exception(message) {

    class NetworkError(message: String? = "Network error") : ApiException(message)
    class Unauthorized(message: String? = "Unauthorized", code: Int = 401) : ApiException(message, code)
    class Forbidden(message: String? = "Forbidden", code: Int = 403) : ApiException(message, code)
    class NotFound(message: String? = "Not Found", code: Int = 404) : ApiException(message, code)
    class ServerError(message: String? = "Server error", code: Int? = null) : ApiException(message, code)
    class UnknownError(message: String? = "Unknown error", code: Int? = null) : ApiException(message, code)
}