package com.example.learnkotlin.core.network

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.ParameterizedType

class BaseResponseAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType
        if (rawType != BaseResponse::class.java) return null

        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter?, value: T?) {
                gson.getAdapter(type).write(out, value)
            }

            override fun read(reader: JsonReader?): T {
                val element = JsonParser.parseReader(reader)
                val dataField = (type.type as ParameterizedType).actualTypeArguments[0]
                val adapter = gson.getAdapter(TypeToken.get(dataField))
                return if (element.isJsonArray) {
                    BaseResponse(data = adapter.fromJsonTree(element)) as T
                } else {
                    gson.getAdapter(type).fromJsonTree(element)
                }
            }
        }
    }
}
