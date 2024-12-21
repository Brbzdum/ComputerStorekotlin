package ru.xdd.computer_store.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import ru.xdd.computer_store.model.CartItemEntity

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity): Long

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItemsForUserFlow(userId: Long): Flow<List<CartItemEntity>> // Изменено

    @Query("DELETE FROM cart_items WHERE cartItemId = :id")
    suspend fun deleteCartItemById(id: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCartForUser(userId: Long)
}
