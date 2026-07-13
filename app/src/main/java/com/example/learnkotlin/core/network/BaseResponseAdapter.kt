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

    override fun <T> create(
        gson: Gson,
        type: TypeToken<T>
    ): TypeAdapter<T>? {

        if (type.rawType != BaseResponse::class.java) {
            return null
        }

        val delegate = gson.getDelegateAdapter(this, type)

        return object : TypeAdapter<T>() {

            override fun write(out: JsonWriter, value: T) {
                delegate.write(out, value)
            }

            override fun read(reader: JsonReader): T {

                val element = JsonParser.parseReader(reader)

                val dataType =
                    (type.type as ParameterizedType)
                        .actualTypeArguments[0]

                val adapter =
                    gson.getAdapter(TypeToken.get(dataType))

                return if (element.isJsonArray) {

                    BaseResponse(
                        data = adapter.fromJsonTree(element)
                    ) as T

                } else {

                    delegate.fromJsonTree(element)

                }
            }
        }
    }
}