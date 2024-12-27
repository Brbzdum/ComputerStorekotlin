package ru.xdd.computer_store.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.xdd.computer_store.model.OrderEntity
import ru.xdd.computer_store.model.OrderItemEntity
import ru.xdd.computer_store.model.OrderStatus

@Dao
interface OrderDao {
    @Query(
        """
        SELECT COUNT(*) > 0 
        FROM orders o
        JOIN order_items oi ON o.orderId = oi.orderId
        WHERE o.userId = :userId AND oi.productId = :productId AND o.orderStatus = :status
        """
    )
    suspend fun hasCompletedOrderForProduct(userId: Long, productId: Long, status: OrderStatus = OrderStatus.ЗАВЕРШЁН): Boolean

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Long): OrderEntity?

    @Query("SELECT * FROM orders")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>


    @Query("UPDATE orders SET orderStatus = :newStatus WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus)

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrder(orderId: Long)



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
    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity>

    @Query("SELECT * FROM orders WHERE userId = :userId")
    suspend fun getOrdersByUserId(userId: Long): List<OrderEntity>

}
