package ru.xdd.computer_store.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.xdd.computer_store.model.OrderEntity
import ru.xdd.computer_store.model.OrderItemEntity

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertOrderItems(orderItems: List<OrderItemEntity>)

    @Query("SELECT * FROM orders WHERE userId = :userId")
    fun getOrdersForUserFlow(userId: Long): Flow<List<OrderEntity>> // Изменено

    @Transaction
    suspend fun placeOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        val orderId = insertOrder(order)
        val itemsWithOrderId = items.map { it.copy(orderId = orderId) }
        insertOrderItems(itemsWithOrderId)
    }

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity> // Этот метод пока оставляем без изменений, так как он используется только в одном месте, где Flow не требуется
}
