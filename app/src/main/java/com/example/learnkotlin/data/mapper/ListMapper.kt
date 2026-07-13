package com.example.learnkotlin.data.mapper

fun <I, O> List<I>.mapTo(
    mapper: Mapper<I, O>
): List<O> = map(mapper::map)