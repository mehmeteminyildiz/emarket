package com.mey.emarket.features.cart.data

import com.mey.emarket.features.favorite.data.FavoritesDao
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    suspend fun addOrIncrementProduct(product: CartEntity) {
        val existingItem = cartDao.getCartItemById(product.id)
        if (existingItem != null) {
            cartDao.incrementQuantity(product.id, 1)
        } else {
            cartDao.addOrUpdateCartItem(product)
        }
    }

    suspend fun decrementProduct(productId: String) {
        cartDao.decrementQuantity(productId, 1)
        cartDao.removeZeroQuantityItems()
    }

    suspend fun getAllCartItems(): List<CartEntity> {
        return cartDao.getAllCartItems()
    }
}