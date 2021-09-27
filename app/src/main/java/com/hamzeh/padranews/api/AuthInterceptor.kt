package com.hamzeh.padranews.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(
    private val headerKey: String,
    private val headerValue: String
    ): Interceptor {

//    private val credentials: String = token

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header(headerKey, headerValue)
            .build()

        return chain.proceed(authenticatedRequest)
    }
}