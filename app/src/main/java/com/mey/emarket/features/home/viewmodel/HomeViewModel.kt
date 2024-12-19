package com.mey.emarket.features.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mey.emarket.core.data.repository.RemoteRepository
import com.mey.emarket.core.utils.Resource
import com.mey.emarket.core.utils.toDate
import com.mey.emarket.core.utils.toDoubleOrZero
import com.mey.emarket.features.favorite.data.FavoritesRepository
import com.mey.emarket.features.favorite.data.SortOption
import com.mey.emarket.features.filter.data.model.FilterModel
import com.mey.emarket.features.home.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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

    // Tüm ürünleri saklayan LiveData
    private val _productsResponse = MutableLiveData<Resource<List<Product>>?>()
    val productsResponse: LiveData<Resource<List<Product>>?> get() = _productsResponse

    // Filtreleme sonuçları için LiveData
    private val _searchResults = MutableLiveData<Resource<List<Product>>?>()
    val searchResults: LiveData<Resource<List<Product>>?> get() = _searchResults

    // Markalar ve modeller
    private val _allBrands = MutableLiveData<List<FilterModel>>()
    val allBrands: LiveData<List<FilterModel>> get() = _allBrands

    private val _allModels = MutableLiveData<List<FilterModel>>()
    val allModels: LiveData<List<FilterModel>> get() = _allModels

    // Kullanıcı seçimlerini saklamak için StateFlow
    val searchQuery = MutableStateFlow("")
    private val selectedBrands = MutableStateFlow<List<String>>(emptyList())
    private val selectedModels = MutableStateFlow<List<String>>(emptyList())
    val selectedSortOption = MutableStateFlow(SortOption.DATE_ASCENDING)

    init {
        observeCombinedFilters()
        viewModelScope.launch {
            fetchAllProducts() // İlk açılışta tüm ürünleri yükle
        }
    }

    private fun observeCombinedFilters() {
        viewModelScope.launch {
            combine(
                searchQuery.debounce(0),
                selectedBrands,
                selectedModels,
                selectedSortOption
            ) { query, brands, models, sortOption ->
                Triple(query, Pair(brands, models), sortOption)
            }.distinctUntilChanged()
                .collect { (query, filters, sortOption) ->
                    val (brands, models) = filters
                    filterProducts(query, brands, models, sortOption)
                }
        }
    }
    // Ürünleri filtreleme ve sıralama işlemi
    private suspend fun filterProducts(
        query: String,
        brands: List<String>,
        models: List<String>,
        sortOption: SortOption
    ) {
        _searchResults.postValue(Resource.Loading())
        val allProducts = repository.getProducts().body() ?: listOf()
        val favoriteIds = favRepository.getAllFavorites().map { it.id }

        // Filtreleme işlemi
        var filteredProducts = allProducts.filter { product ->
            (brands.isEmpty() || product.brand in brands) &&
                    (models.isEmpty() || product.model in models) &&
                    (query.isBlank() || product.name!!.contains(query, ignoreCase = true))
        }

        // Sıralama işlemi
        filteredProducts = when (sortOption) {
            SortOption.DATE_DESCENDING -> filteredProducts.sortedByDescending { it.createdAt?.toDate() }
            SortOption.DATE_ASCENDING -> filteredProducts.sortedBy { it.createdAt?.toDate() }
            SortOption.PRICE_DESCENDING -> filteredProducts.sortedByDescending { it.price?.toDoubleOrZero() }
            SortOption.PRICE_ASCENDING -> filteredProducts.sortedBy { it.price?.toDoubleOrZero() }
        }

        // Favori ürünleri işaretleme
        filteredProducts.forEach { product ->
            product.isFavorite = product.id in favoriteIds
        }

        _searchResults.postValue(Resource.Success(filteredProducts))
    }
    // Kullanıcı seçimi güncellemeleri
    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun reloadFilteredProducts() {
        viewModelScope.launch {
            val currentQuery = searchQuery.value
            val currentBrands = selectedBrands.value
            val currentModels = selectedModels.value
            val currentSort = selectedSortOption.value

            filterProducts(currentQuery, currentBrands, currentModels, currentSort)
        }
    }

    fun updateSelectedBrands(brands: List<String>) {
        selectedBrands.value = brands
    }

    fun updateSelectedModels(models: List<String>) {
        selectedModels.value = models
    }

    fun updateSortOption(option: SortOption) {
        selectedSortOption.value = option
    }

    // Tüm ürünleri yükleme ve markaları/modelleri belirleme
     fun fetchAllProducts() = viewModelScope.launch {
        _productsResponse.postValue(Resource.Loading())
        val response = repository.getProducts()
        val productList = response.body() ?: listOf()
        val favoriteIds = favRepository.getAllFavorites().map { it.id }

        productList.forEach {
            it.isFavorite = it.id in favoriteIds
        }

        _allBrands.postValue(
            productList.mapNotNull { it.brand?.let { brand -> FilterModel(brand, false) } }
                .distinctBy { it.name }
        )

        _allModels.postValue(
            productList.mapNotNull { it.model?.let { model -> FilterModel(model, false) } }
                .distinctBy { it.name }
        )

        _productsResponse.postValue(Resource.Success(productList))
    }
}
