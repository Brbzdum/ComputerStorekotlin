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

    @Query("SELECT * FROM products")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?


    // Получение основных продуктов (без аксессуаров)
    @Query("SELECT * FROM products WHERE parentProductId IS NULL")
    fun getMainProductsFlow(): Flow<List<ProductEntity>>

    // Получение аксессуаров для продукта
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
    /**
     * Поиск продуктов по названию.
     * @param query Часть названия продукта для поиска.
     * @return Поток списка продуктов, соответствующих запросу.
     */
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    fun searchProductsByName(query: String): Flow<List<ProductEntity>>

    /**
     * Фильтрация продуктов по категории.
     * @param category Название категории.
     * @return Поток списка продуктов в заданной категории.
     */
    @Query("SELECT * FROM products WHERE category = :category")
    fun filterProductsByCategory(category: String): Flow<List<ProductEntity>>

    /**
     * Фильтрация продуктов по диапазону цен.
     * @param minPrice Минимальная цена.
     * @param maxPrice Максимальная цена.
     * @return Поток списка продуктов в заданном ценовом диапазоне.
     */
    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice")
    fun filterProductsByPriceRange(minPrice: Double, maxPrice: Double): Flow<List<ProductEntity>>



    @Transaction
    suspend fun removeAccessoryFromProduct(productId: Long, accessoryId: Long) {
        deleteProductAccessoryCrossRef(ProductAccessoryCrossRef(productId, accessoryId))
    }

    // Добавленный метод для обновления рейтинга продукта
    @Query("UPDATE products SET rating = :rating WHERE productId = :productId")
    suspend fun updateRating(productId: Long, rating: Float)

    // Метод для получения продукта с аксессуарами
    @Transaction
    @Query("SELECT * FROM products WHERE productId = :productId")
    fun getProductWithAccessoriesFlow(productId: Long): Flow<ProductWithAccessories>
}
