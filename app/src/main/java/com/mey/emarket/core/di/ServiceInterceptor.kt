package com.mey.emarket.core.di

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ServiceInterceptor
@Inject constructor(
): Interceptor
{
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val requestBuilder : Request.Builder = original.newBuilder()
        val request : Request = requestBuilder.build()
        return chain.proceed(request)
    }
}
