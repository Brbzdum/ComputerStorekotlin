package ru.xdd.computer_store.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.xdd.computer_store.model.*

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProductById(productId: Long)

    @Query("SELECT * FROM products")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?

    @Query("""
        SELECT p.*
        FROM products p
        INNER JOIN product_accessories pa ON p.productId = pa.accessoryId
        WHERE pa.productId = :parentId
    """)
    fun getAccessoriesForProductFlow(parentId: Long): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductAccessoryCrossRef(crossRef: ProductAccessoryCrossRef)

    @Delete
    suspend fun deleteProductAccessoryCrossRef(crossRef: ProductAccessoryCrossRef)

    @Transaction
    suspend fun addAccessoryToProduct(productId: Long, accessoryId: Long) {
        insertProductAccessoryCrossRef(ProductAccessoryCrossRef(productId, accessoryId))
    }

    @Transaction
    suspend fun removeAccessoryFromProduct(productId: Long, accessoryId: Long) {
        deleteProductAccessoryCrossRef(ProductAccessoryCrossRef(productId, accessoryId))
    }

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    fun searchProductsByName(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category")
    fun filterProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice")
    fun filterProductsByPriceRange(minPrice: Double, maxPrice: Double): Flow<List<ProductEntity>>

    @Query("UPDATE products SET rating = :rating WHERE productId = :productId")
    suspend fun updateRating(productId: Long, rating: Float)

    @Transaction
    @Query("SELECT * FROM products WHERE productId = :productId")
    fun getProductWithAccessoriesFlow(productId: Long): Flow<ProductWithAccessories>
}

