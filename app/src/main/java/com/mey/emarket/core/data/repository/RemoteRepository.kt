package com.mey.emarket.core.data.repository

import com.mey.emarket.core.data.network.RemoteService
import com.mey.emarket.core.di.ServiceEMarket
import com.mey.emarket.features.home.data.model.Product
import retrofit2.Response
import javax.inject.Inject

class RemoteRepository @Inject constructor(
    @ServiceEMarket private val service: RemoteService,
) {

    suspend fun getProducts() : Response<List<Product>>{
        return service.getProducts()
    }
}