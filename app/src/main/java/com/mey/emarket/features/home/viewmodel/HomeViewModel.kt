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
    private var _productsResponse: MutableLiveData<Resource<List<Product>>?> = MutableLiveData()
    val productsResponse: LiveData<Resource<List<Product>>?> get() = _productsResponse

    private val searchQuery = MutableStateFlow<String>("")
    val searchResults = MutableLiveData<Resource<List<Product>>?>()

    private var _allBrands: MutableLiveData<List<FilterModel>> = MutableLiveData()
    val allBrands: LiveData<List<FilterModel>> get() = _allBrands

    private var _allModels: MutableLiveData<List<FilterModel>> = MutableLiveData()
    val allModels: LiveData<List<FilterModel>> get() = _allModels

    private var _selectedSort: MutableLiveData<SortOption> = MutableLiveData()
    val selectedSort : LiveData<SortOption> get() = _selectedSort

    fun setAllBrands(brands: List<FilterModel>) = viewModelScope.launch {
        _allBrands.postValue(brands)
    }

    fun setAllModels(models: List<FilterModel>) = viewModelScope.launch {
        _allModels.postValue(models)
    }

    fun getSelectedSort(): SortOption{
        return selectedSortOption.value?: SortOption.DATE_ASCENDING
    }

    private val selectedBrands = MutableStateFlow<List<String>>(emptyList())
    private val selectedModels = MutableStateFlow<List<String>>(emptyList())
    private val selectedSortOption = MutableStateFlow<SortOption?>(SortOption.DATE_ASCENDING)


    init {
        observeCombinedFilters()
        viewModelScope.launch {
            fetchAllProducts() // İlk açılışta tüm ürünleri yükle
        }
    }



    private fun observeCombinedFilters() {
        viewModelScope.launch {
            combine(
                searchQuery.debounce(200),
                selectedBrands,
                selectedModels,
                selectedSortOption
            ) { query, brands, models,sortOption ->
                Triple(query, Pair(brands, models), sortOption)
            }.distinctUntilChanged()
                .collect { (query, filters, sortOption) ->
                    val (brands, models) = filters
                    filterProducts(query, brands, models)
                }
        }
    }

    private suspend fun filterProducts(query: String, brands: List<String>, models: List<String>) {
        searchResults.postValue(Resource.Loading())
        val allProducts = repository.getProducts().body() ?: listOf()
        val favoriteIds = favRepository.getAllFavorites().map { it.id }

        var filteredProducts = allProducts.filter { product ->
            (brands.isEmpty() || product.brand in brands) &&
                    (models.isEmpty() || product.model in models) &&
                    (query.isBlank() || product.name!!.contains(query, ignoreCase = true))
        }

        selectedSortOption.value?.let { sortOption ->
            filteredProducts = when (sortOption) {
                SortOption.DATE_DESCENDING -> filteredProducts.sortedByDescending { it.createdAt?.toDate() }
                SortOption.DATE_ASCENDING -> filteredProducts.sortedBy { it.createdAt?.toDate() }
                SortOption.PRICE_DESCENDING -> filteredProducts.sortedByDescending { it.price?.toDoubleOrZero() }
                SortOption.PRICE_ASCENDING -> filteredProducts.sortedBy { it.price?.toDoubleOrZero() }
            }
        }

        // Favori ürünleri işaretle
        filteredProducts.forEach {
            if (it.id in favoriteIds) it.isFavorite = true
        }

        searchResults.postValue(Resource.Success(filteredProducts))
    }

    // Seçili markaları güncelle
    fun updateSelectedBrands(brands: List<String>) {
        selectedBrands.value = brands
    }

    fun updateSelectedModels(models: List<String>) {
        selectedModels.value = models
    }

    // Arama sorgusunu güncelle
    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateSortOption(option: SortOption) {
        selectedSortOption.value = option
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

        setAllBrands(
            productList
                .map { FilterModel(name = it.brand ?: "", isSelected = false) }
                .distinctBy { it.name }
        )
        setAllModels(
            productList
                .map { FilterModel(name = it.model ?: "", isSelected = false) }
                .distinctBy { it.name }
        )

        _productsResponse.postValue(Resource.Success(productList))
    }

    fun clearProductsResponse() {
        _productsResponse.postValue(null)
    }


    private suspend fun fetchAllProducts() {
        searchResults.postValue(Resource.Loading())
        val allProducts = repository.getProducts().body() ?: listOf()
        val favoriteIds = favRepository.getAllFavorites().map { it.id }

        allProducts.forEach {
            if (it.id in favoriteIds) it.isFavorite = true
        }

        searchResults.postValue(Resource.Success(allProducts))
    }
}
