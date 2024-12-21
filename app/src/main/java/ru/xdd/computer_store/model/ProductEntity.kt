package ru.xdd.computer_store.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.xdd.computer_store.utils.Converters

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["parentProductId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["parentProductId"])]
)
@TypeConverters(Converters::class) // Добавляем TypeConverters для работы со списком отзывов
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val productId: Long = 0,
    val parentProductId: Long? = null, // если товар является аксессуаром, указываем основной товар
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val rating: Float = 0.0f, // Рейтинг товара (от 0 до 5)
    val reviews: List<String> = emptyList() // Список отзывов о товаре
)
