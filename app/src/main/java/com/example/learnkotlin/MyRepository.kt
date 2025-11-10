package com.example.learnkotlin

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MyRepository {
    suspend fun fetchFromNetwork(): String {
        delay(2000)
        return "Dữ liệu từ server"
    }

    // Hàm fetch số dư account
    suspend fun fetchBalance(): Float {
        delay(1500) // giả lập network call
        return 12345.67f
    }

    // Hàm fetch danh sách giao dịch
    suspend fun fetchTransactions(): List<String> {
        delay(2000) // giả lập network call
        return listOf(
            "Transaction 1: -100",
            "Transaction 2: +500",
            "Transaction 3: -50"
        )
    }

    fun searchItems(query: String): Flow<List<String>> = flow {
        delay(1000) // giả lập network latency
        val result = listOf(
            "$query - Result 1",
            "$query - Result 2",
            "$query - Result 3"
        )
        emit(result)
    }

}