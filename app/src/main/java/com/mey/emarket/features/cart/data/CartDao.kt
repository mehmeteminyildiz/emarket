package com.mey.emarket.features.cart.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartDao {

    // Sepete ürün ekleme veya güncelleme
    @Query("SELECT * FROM cart WHERE id = :productId LIMIT 1")
    suspend fun getCartItemById(productId: String): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdateCartItem(cartItem: CartEntity)

    // Sepetteki ürünün miktarını artırma
    @Query("UPDATE cart SET quantity = quantity + :increment WHERE id = :productId")
    suspend fun incrementQuantity(productId: String, increment: Int)

    // Sepetteki ürünün miktarını azaltma
    @Query("UPDATE cart SET quantity = quantity - :decrement WHERE id = :productId AND quantity > 0")
    suspend fun decrementQuantity(productId: String, decrement: Int)

    // Sepetteki ürün miktarını kontrol et ve sıfır olanları sil
    @Query("DELETE FROM cart WHERE quantity <= 0")
    suspend fun removeZeroQuantityItems()

    // Tüm cart öğelerini listeleme
    @Query("SELECT * FROM cart")
    suspend fun getAllCartItems(): List<CartEntity>


}