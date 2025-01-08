package ru.xdd.computer_store.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.xdd.computer_store.model.CartItemEntity

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItemsForUserFlow(userId: Long): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    fun getCartItemByUserIdAndProductIdFlow(userId: Long, productId: Long): Flow<CartItemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE cartItemId = :cartItemId")
    suspend fun deleteCartItemById(cartItemId: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCartForUser(userId: Long)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE cartItemId = :cartItemId")
    suspend fun updateCartItemQuantity(cartItemId: Long, quantity: Long)
}


