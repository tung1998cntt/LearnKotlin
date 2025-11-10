package com.example.learnkotlin.data.repository

import com.example.learnkotlin.data.remote.ApiService
import kotlinx.coroutines.delay

class DataRepository(private val apiService: ApiService) {

    suspend fun getData(): String {
        // Có thể thêm cache, DB... nếu cần
        return apiService.fetchDataFromNetwork()
    }

    suspend fun getTransactionList(): List<String> {
        delay(1000)
        return listOf("Payment 1", "Payment 2", "Transfer 3")
    }
}