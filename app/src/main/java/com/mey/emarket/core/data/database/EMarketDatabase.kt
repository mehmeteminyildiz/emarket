package com.mey.emarket.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mey.emarket.features.cart.data.CartDao
import com.mey.emarket.features.cart.data.CartEntity
import com.mey.emarket.features.favorite.data.FavoritesDao
import com.mey.emarket.features.favorite.data.FavoritesEntity


@Database(
    entities = [FavoritesEntity::class, CartEntity::class], // Kullanılacak Entity'ler
    version = 1, // Veritabanı versiyonu
    exportSchema = false // Şema dosyasını export etmek istemiyorsanız false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
    abstract fun cartDao(): CartDao
}