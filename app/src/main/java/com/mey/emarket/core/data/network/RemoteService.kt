package com.mey.emarket.core.data.network

import com.mey.emarket.features.home.data.model.Product
import retrofit2.Response
import retrofit2.http.GET

interface RemoteService {

    @GET(EndPoints.PRODUCTS)
    suspend fun getProducts(): Response<List<Product>>


}