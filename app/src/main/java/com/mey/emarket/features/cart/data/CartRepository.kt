package com.mey.emarket.features.cart.data

import com.mey.emarket.features.favorite.data.FavoritesDao
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    // Sepete ürün ekleme veya miktar artırma
    suspend fun addOrIncrementProduct(product: CartEntity) {
        val existingItem = cartDao.getCartItemById(product.id)
        if (existingItem != null) {
            cartDao.incrementQuantity(product.id, 1)
        } else {
            cartDao.addOrUpdateCartItem(product)
        }
    }

    // Sepetteki ürün miktarını azaltma
    suspend fun decrementProduct(productId: String) {
        cartDao.decrementQuantity(productId, 1)
        cartDao.removeZeroQuantityItems()
    }

    // Tüm cart öğelerini alma
    suspend fun getAllCartItems(): List<CartEntity> {
        return cartDao.getAllCartItems()
    }
}