package com.example.learnkotlin.di

import android.content.Context
import com.example.learnkotlin.core.constants.Tags
import com.example.learnkotlin.core.network.ApiService
import com.example.learnkotlin.core.network.AuthInterceptor
import com.example.learnkotlin.core.network.BaseResponseAdapterFactory
import com.example.learnkotlin.core.network.GeocodingApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MapTilerRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MapTilerClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl() = Tags.BASE_URL

    @Provides
    @Singleton
    @ApiClient
    fun provideOkHttpClient(
        cache: Cache,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(authInterceptor) // AuthInterceptor đã xử lý lấy token từ cache và add vào header
            .addInterceptor(logging)
            .connectTimeout(Tags.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Tags.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Tags.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    @MapTilerClient
    fun provideMapTilerOkHttpClient(
        cache: Cache
    ): OkHttpClient {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(logging)
            .connectTimeout(Tags.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Tags.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Tags.DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    @MapTilerRetrofit
    fun provideMapTilerRetrofit(
        @MapTilerClient client: OkHttpClient
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(Tags.MAPTILER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideGeocodingApi(
        @MapTilerRetrofit
        retrofit: Retrofit
    ): GeocodingApi {
        return retrofit.create(GeocodingApi::class.java)

    }

    @Provides
    @Singleton
    @ApiRetrofit
    fun provideRetrofit(@ApiClient client: OkHttpClient, baseUrl: String, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideApiService(
        @ApiRetrofit retrofit: Retrofit
    ): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val builder = GsonBuilder()
            .registerTypeAdapterFactory(BaseResponseAdapterFactory())
            .serializeNulls()
        return builder.create()
    }

    @Provides
    @Singleton
    fun provideCache(
        @ApplicationContext context: Context
    ): Cache {

        val cacheSize = 10L * 1024 * 1024
        return Cache(
            File(context.cacheDir, "http-cache"),
            cacheSize
        )
    }

    class NullOnEmptyConverterFactory : Converter.Factory() {

        override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *> {

            val delegate: Converter<ResponseBody, Any> =
                retrofit.nextResponseBodyConverter(this, type, annotations)

            return Converter { body ->
                if (body.contentLength() == 0L) {
                    null
                } else {
                    delegate.convert(body)
                }
            }
        }
    }

}