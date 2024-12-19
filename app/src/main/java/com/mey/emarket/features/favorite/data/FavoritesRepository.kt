package com.mey.emarket.features.favorite.data

import com.mey.emarket.features.home.data.model.Product
import com.mey.emarket.shared.data.mapper.toFavoriteEntity
import javax.inject.Inject

class FavoritesRepository
@Inject constructor(
    private val favoritesDao: FavoritesDao
) {
    suspend fun getAllFavorites(): List<FavoritesEntity> {
        return favoritesDao.getAllFavorites()
    }

    suspend fun addFavorite(item: Product) {
        favoritesDao.addFavorite(item.toFavoriteEntity())
    }

    suspend fun removeFavorite(product: Product) {
        favoritesDao.removeFavorite(product.toFavoriteEntity())
    }
}