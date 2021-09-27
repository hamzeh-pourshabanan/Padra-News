package com.hamzeh.padranews.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://newsapi.org/"
private const val AUTH_HEADER_KEY = "X-Api-Key"
private const val AUTH_HEADER_VALUE = "09f77ddaa16849c5a112e9eff8f03d64"
private const val AUTH_HEADER_VALUE_SPARE = "9f47f55b27c04449a481850d8cc68198"

interface NewsService {

    @GET("v2/top-headlines")
    suspend fun getHeadLines(
        @Query("language") query: String = "en",
        @Query("page") page: Int = 1,
        @Query("pageSize") itemsPerPage: Int = 20
    ): HeadlineResponse

    companion object {
        fun create(): NewsService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(AuthInterceptor(AUTH_HEADER_KEY, AUTH_HEADER_VALUE))
                .addInterceptor(AuthInterceptor(AUTH_HEADER_KEY, AUTH_HEADER_VALUE_SPARE))
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .client(client)
                .build()
                .create(NewsService::class.java)
        }
    }
}