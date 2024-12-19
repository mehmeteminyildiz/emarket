package com.mey.emarket.features.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mey.emarket.core.data.repository.RemoteRepository
import com.mey.emarket.core.utils.Resource
import com.mey.emarket.features.favorite.data.FavoritesRepository
import com.mey.emarket.features.home.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel
@Inject constructor(
    application: Application,
    private val repository: RemoteRepository,
    private val favRepository: FavoritesRepository,
) : AndroidViewModel(application) {
    private var _productsResponse: MutableLiveData<Resource<List<Product>>?> = MutableLiveData()
    val productsResponse: LiveData<Resource<List<Product>>?> get() = _productsResponse

    private val searchQuery = MutableStateFlow<String>("")
    val searchResults = MutableLiveData<Resource<List<Product>>?>()

    init {
        observeSearchQuery()
        viewModelScope.launch {
            fetchAllProducts() // İlk açılışta tüm ürünleri yükle
        }
    }


    fun getProducts() = viewModelScope.launch {
        _productsResponse.postValue(Resource.Loading())
        val response = repository.getProducts()
        val productList = response.body() ?: listOf()
        val favoriteIds = favRepository.getAllFavorites().map { it.id }


        productList.forEach {
            if (it.id in favoriteIds)
                it.isFavorite = true
        }

        _productsResponse.postValue(Resource.Success(productList))
    }

    fun clearProductsResponse() {
        _productsResponse.postValue(null)
    }

    // Arama sorgusu için Flow'u dinle
    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(200) // 1.5 saniye gecikme
                .distinctUntilChanged() // Aynı sorguyu birden fazla kez işleme
                .collect { query ->
                    if (query.isBlank()) {
                        fetchAllProducts()
                    } else {
                        searchProducts(query)
                    }
                }
        }
    }

    private suspend fun fetchAllProducts() {
        searchResults.postValue(Resource.Loading())
        val allProducts = repository.getProducts().body() ?: listOf()
        val favoriteIds = favRepository.getAllFavorites().map { it.id }

        allProducts.forEach {
            if (it.id in favoriteIds)
                it.isFavorite = true
        }

        searchResults.postValue(Resource.Success(allProducts))
    }

    // Arama işlemi
    private suspend fun searchProducts(query: String) {
        searchResults.postValue(Resource.Loading())
        val allProducts = repository.getProducts().body() ?: listOf()
        val filteredProducts = allProducts.filter {
            it.name!!.contains(query, ignoreCase = true)
        }

        val favoriteIds = favRepository.getAllFavorites().map { it.id }

        filteredProducts.forEach {
            if (it.id in favoriteIds)
                it.isFavorite = true
        }

        searchResults.postValue(Resource.Success(filteredProducts))
    }

    // Arama sorgusunu güncelle
    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }
}
