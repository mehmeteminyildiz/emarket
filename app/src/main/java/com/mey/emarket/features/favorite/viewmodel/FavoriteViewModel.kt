package com.mey.emarket.features.favorite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mey.emarket.core.data.repository.RemoteRepository
import com.mey.emarket.features.favorite.data.FavoritesEntity
import com.mey.emarket.features.favorite.data.FavoritesRepository
import com.mey.emarket.features.home.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel
@Inject constructor(
    application: Application,
    private val repository: FavoritesRepository,
    private val remoteRepository: RemoteRepository,
) : AndroidViewModel(application) {

    private val _favItems = MutableLiveData<List<FavoritesEntity>?>()
    val favItems: LiveData<List<FavoritesEntity>?> get() = _favItems

    fun fetchFavorites() = viewModelScope.launch {
        _favItems.postValue(repository.getAllFavorites())
    }

    fun addToFavorites(product: Product) = viewModelScope.launch {
        repository.addFavorite(product)
        fetchFavorites()

    }

    fun removeFromFavorites(product: Product) = viewModelScope.launch {
        repository.removeFavorite(product)
        fetchFavorites()
    }

    fun clearFavItems(){
        _favItems.postValue(null)
    }

}