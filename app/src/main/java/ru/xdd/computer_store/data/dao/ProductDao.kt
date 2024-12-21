package ru.xdd.computer_store.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.xdd.computer_store.model.ProductEntity

@Dao
interface ProductDao {
    @Insert
    suspend fun insertProduct(product: ProductEntity): Long

    @Query("SELECT * FROM products")
    fun getAllProductsFlow(): Flow<List<ProductEntity>> // Изменено

    @Query("SELECT * FROM products WHERE productId = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity? // Этот метод оставляем без изменений, так как он используется для получения одного товара по ID

    @Query("SELECT * FROM products WHERE parentProductId IS NULL")
    fun getMainProductsFlow(): Flow<List<ProductEntity>> // Изменено

    @Query("SELECT * FROM products WHERE parentProductId = :parentId")
    fun getAccessoriesForProductFlow(parentId: Long): Flow<List<ProductEntity>> // Изменено

    @Query("UPDATE products SET rating = :rating WHERE productId = :productId")
    suspend fun updateRating(productId: Long, rating: Float)

    @Update
    suspend fun updateProduct(product: ProductEntity)
}
