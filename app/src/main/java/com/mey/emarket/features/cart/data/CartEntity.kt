package com.mey.emarket.features.cart.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val id: String, // Ürün ID'si benzersiz olacak
    var createdAt: String,
    var name: String,
    var image: String,
    var price: String,
    var description: String,
    var model: String,
    var brand: String,
    var quantity: Int
)

