package com.mey.emarket.core.utils

import com.mey.emarket.features.cart.data.CartEntity
import com.mey.emarket.features.favorite.data.FavoritesEntity
import com.mey.emarket.features.home.data.Product

// Product -> FavoritesEntity
fun Product.toFavoriteEntity(): FavoritesEntity {
    return FavoritesEntity(
        id = this.id ?: "",
        createdAt = this.createdAt ?: "",
        name = this.name ?: "",
        image = this.image ?: "",
        price = this.price ?: "",
        description = this.description ?: "",
        model = this.model ?: "",
        brand = this.brand ?: ""

    )
}

fun Product.toCartEntity(quantity: Int=1): CartEntity {
    return CartEntity(
        id = this.id ?: "",
        createdAt = this.createdAt ?: "",
        name = this.name ?: "",
        image = this.image ?: "",
        price = this.price ?: "",
        description = this.description ?: "",
        model = this.model ?: "",
        brand = this.brand ?: "",
        quantity = quantity

    )
}


// Entity -> Product
fun FavoritesEntity.toProduct(): Product {
    return Product(
        createdAt = this.createdAt,
        name = this.name,
        image = this.image,
        price = this.price,
        description = this.description,
        model = this.model,
        brand = this.brand,
        id = this.id,
        isFavorite = true
    )
}
fun CartEntity.toProduct(): Product {
    return Product(
        createdAt = this.createdAt,
        name = this.name,
        image = this.image,
        price = this.price,
        description = this.description,
        model = this.model,
        brand = this.brand,
        id = this.id,


    )
}