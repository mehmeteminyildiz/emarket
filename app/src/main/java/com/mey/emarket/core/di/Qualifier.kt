package com.mey.emarket.core.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServiceEMarket



@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EMarketOkhttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitEMarket
