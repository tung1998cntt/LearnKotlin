package com.example.learnkotlin.core.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.learnkotlin.core.network.ApiException
import com.example.learnkotlin.core.network.ApiResult
import com.example.learnkotlin.core.network.BaseResponse


suspend fun <T> safeApiCall(apiCall: suspend () -> BaseResponse<T>): ApiResult<T> {
    return try {
        val response = apiCall()
        if ((response.code ?: 200) in 200..299 && response.data != null) {
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

suspend fun <T> safeApiCallNotBase(
    apiCall: suspend () -> T
): ApiResult<T> {
    return try {
        ApiResult.Success(apiCall())
    } catch (e: Exception) {
        val code = (e as? ApiException)?.code
        ApiResult.Error(
            code.toString(),
            e.message ?: "Unknown error"
        )
    }
}

fun View.setSafeOnClick(interval: Long = 600L, onClick: (View) -> Unit) {
    var lastClickTime = 0L

    setOnClickListener { v ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= interval) {
            lastClickTime = currentTime
            onClick(v)
        }
    }
}

fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
}
