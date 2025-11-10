package com.example.learnkotlin

import kotlinx.coroutines.delay

class MyRepository {
    suspend fun fetchFromNetwork(): String {
        delay(2000)
        return "Dữ liệu từ server"
    }
}