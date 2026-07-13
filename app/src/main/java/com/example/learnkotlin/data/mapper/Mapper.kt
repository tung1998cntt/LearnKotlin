package com.example.learnkotlin.data.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}