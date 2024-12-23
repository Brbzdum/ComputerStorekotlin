package ru.xdd.computer_store.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class OrderStatus {
    НОВЫЙ,
    В_ПРОЦЕССЕ,
    ЗАВЕРШЁН
}

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val orderId: Long = 0,
    val userId: Long,
    val orderDate: Long,
    val orderStatus: OrderStatus,
    val totalAmount: Double,
    val shippingAddress: String
)
