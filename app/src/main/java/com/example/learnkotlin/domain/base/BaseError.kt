package com.example.learnkotlin.domain.base

sealed class BaseError(val message: String?, val code: Int? = null) {
    class NetworkError(message: String?) : BaseError(message)
    class Unauthorized(message: String?) : BaseError(message, 401)
    class Forbidden(message: String?) : BaseError(message, 403)
    class ServerError(message: String?, code: Int? = null) : BaseError(message, code)
    class UnknownError(message: String?) : BaseError(message)
}