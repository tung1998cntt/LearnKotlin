package com.example.learnkotlin.data.remote

import kotlinx.coroutines.delay

class ApiService {

    suspend fun fetchDataFromNetwork(): String {
        delay(3000) // Giả lập network call
        return "Hello from API service"
    }


}