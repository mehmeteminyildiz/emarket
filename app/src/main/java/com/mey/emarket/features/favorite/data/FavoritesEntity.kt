package com.mey.emarket.features.favorite.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey val id: String,
    var createdAt: String,
    var name: String,
    var image: String,
    var price: String,
    var description: String,
    var model: String,
    var brand: String,
)

