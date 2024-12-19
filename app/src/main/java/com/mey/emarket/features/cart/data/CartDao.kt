package com.mey.emarket.features.cart.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartDao {

    @Query("SELECT * FROM cart WHERE id = :productId LIMIT 1")
    suspend fun getCartItemById(productId: String): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdateCartItem(cartItem: CartEntity)

    @Query("UPDATE cart SET quantity = quantity + :increment WHERE id = :productId")
    suspend fun incrementQuantity(productId: String, increment: Int)

    @Query("UPDATE cart SET quantity = quantity - :decrement WHERE id = :productId AND quantity > 0")
    suspend fun decrementQuantity(productId: String, decrement: Int)

    @Query("DELETE FROM cart WHERE quantity <= 0")
    suspend fun removeZeroQuantityItems()

    @Query("SELECT * FROM cart")
    suspend fun getAllCartItems(): List<CartEntity>


}