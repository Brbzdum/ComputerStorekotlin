package ru.xdd.computer_store.data.repository

import android.content.SharedPreferences
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.xdd.computer_store.data.dao.*
import ru.xdd.computer_store.model.*
import javax.inject.Inject

/**
 * Репозиторий для управления данными приложения.
 */
class StoreRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val reviewDao: ReviewDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao
) {

    // --- Методы для работы с SharedPreferences ---

    /**
     * Сохраняет текущего пользователя в SharedPreferences.
     * @param userId ID пользователя.
     * @param role Роль пользователя (например, "USER" или "ADMIN").
     */
    fun saveCurrentUser(userId: Long, role: String) {
        sharedPreferences.edit().apply {
            putLong("userId", userId)
            putString("role", role)
            apply()
        }
    }

    /**
     * Получает текущего пользователя из SharedPreferences.
     * @return Пара `userId` и `role`, или `(-1, null)`, если пользователь не найден.
     */
    fun getCurrentUser(): Pair<Long, String?> {
        val userId = sharedPreferences.getLong("userId", -1L)
        val role = sharedPreferences.getString("role", null)
        return userId to role
    }

    /**
     * Удаляет данные текущего пользователя из SharedPreferences (выход из системы).
     */
    fun logout() {
        sharedPreferences.edit().clear().apply()
    }

    // --- Методы для работы с каталогом ---

    /**
     * Возвращает поток всех товаров в каталоге.
     * @return Поток списка товаров.
     */
    fun getAllProductsFlow(): Flow<List<ProductEntity>> = productDao.getAllProductsFlow()

    /**
     * Возвращает поток аксессуаров для конкретного товара.
     * @param parentId ID основного товара.
     * @return Поток списка аксессуаров.
     */
    fun getAccessoriesForProductFlow(parentId: Long): Flow<List<ProductEntity>> =
        productDao.getAccessoriesForProductFlow(parentId)

    // --- Методы для работы с отзывами ---

    /**
     * Возвращает поток отзывов для конкретного товара.
     * @param productId ID товара.
     * @return Поток списка отзывов.
     */
    fun getReviewsForProductFlow(productId: Long): Flow<List<ReviewEntity>> =
        reviewDao.getReviewsForProductFlow(productId)

    /**
     * Добавляет новый отзыв к товару.
     * @param userId ID пользователя.
     * @param productId ID товара.
     * @param rating Рейтинг (1-5).
     * @param comment Текст отзыва.
     * @throws IllegalArgumentException Если пользователь не завершил покупку этого товара.
     */
    suspend fun addReview(userId: Long, productId: Long, rating: Int, comment: String) {
        val hasPurchased = orderDao.hasCompletedOrderForProduct(userId, productId)
        if (!hasPurchased) throw IllegalArgumentException("Пользователь не завершил покупку данного товара")

        val review = ReviewEntity(
            userId = userId,
            productId = productId,
            rating = rating,
            comment = comment
        )
        reviewDao.insertReview(review)
        updateProductRating(productId)
    }

    /**
     * Удаляет отзыв по ID.
     * @param reviewId ID отзыва.
     */
    suspend fun deleteReview(reviewId: Long) {
        reviewDao.deleteReview(reviewId)
    }

    /**
     * Обновляет средний рейтинг товара после добавления или удаления отзыва.
     * @param productId ID товара.
     */
    private suspend fun updateProductRating(productId: Long) {
        val reviews = reviewDao.getReviewsForProductFlow(productId).first()
        val averageRating =
            if (reviews.isNotEmpty()) reviews.map { it.rating }.average().toFloat() else 0.0f
        productDao.updateRating(productId, averageRating)
    }

    // --- Методы для работы с корзиной ---

    /**
     * Добавляет товар в корзину пользователя.
     * @param userId ID пользователя.
     * @param productId ID товара.
     * @param quantity Количество.
     * @throws IllegalArgumentException Если товара недостаточно на складе.
     */
    suspend fun addToCart(userId: Long, productId: Long, quantity: Int) {
        val product = productDao.getProductById(productId)
            ?: throw IllegalArgumentException("Товар не найден")
        if (product.stock < quantity) throw IllegalArgumentException("Недостаточно товара на складе")

        val existingCartItems = cartDao.getCartItemsForUserFlow(userId).first()
        val existingCartItem = existingCartItems.find { it.productId.toLong() == productId }

        if (existingCartItem != null) {
            val updatedQuantity = existingCartItem.quantity + quantity
            val updatedCartItem = existingCartItem.copy(quantity = updatedQuantity)
            cartDao.insertCartItem(updatedCartItem)
        } else {
            val cartItem = CartItemEntity(
                userId = userId,
                productId = productId,
                quantity = quantity
            )
            cartDao.insertCartItem(cartItem)
        }
    }

    /**
     * Удаляет товар из корзины по ID элемента корзины.
     * @param cartItemId ID элемента корзины.
     */
    suspend fun removeCartItemById(cartItemId: Long) {
        cartDao.deleteCartItemById(cartItemId)
    }

    /**
     * Очищает корзину пользователя.
     * @param userId ID пользователя.
     */
    suspend fun clearCartForUser(userId: Long) {
        cartDao.clearCartForUser(userId)
    }

    /**
     * Возвращает поток товаров в корзине пользователя.
     * @param userId ID пользователя.
     * @return Поток списка товаров в корзине.
     */
    fun getCartItemsForUserFlow(userId: Long): Flow<List<CartItemEntity>> =
        cartDao.getCartItemsForUserFlow(userId)

    // --- Методы для работы с заказами ---

    /**
     * Оформляет заказ на товары в корзине.
     * @param userId ID пользователя.
     * @param items Список товаров с количеством.
     * @param shippingAddress Адрес доставки.
     * @return ID созданного заказа.
     * @throws IllegalArgumentException Если товаров недостаточно на складе.
     */
    suspend fun placeOrder(
        userId: Long,
        items: List<Pair<ProductEntity, Int>>,
        shippingAddress: String
    ): Long {
        items.forEach { (product, quantity) ->
            if (product.stock < quantity) throw IllegalArgumentException("Недостаточно товара ${product.name} на складе")
        }

        val totalAmount = items.sumOf { it.first.price * it.second }
        val order = OrderEntity(
            userId = userId,
            orderDate = System.currentTimeMillis(),
            orderStatus = OrderStatus.НОВЫЙ,
            totalAmount = totalAmount,
            shippingAddress = shippingAddress
        )
        val orderItems = items.map {
            OrderItemEntity(
                orderId = 0, // Устанавливается после транзакции.
                productId = it.first.productId,
                quantity = it.second,
                priceAtOrderTime = it.first.price
            )
        }
        val orderId = placeOrderWithItems(order, orderItems)

        items.forEach { (product, quantity) ->
            val updatedProduct = product.copy(stock = product.stock - quantity)
            productDao.updateProduct(updatedProduct)
        }

        clearCartForUser(userId)

        return orderId
    }

    /**
     * Меняет статус заказа.
     * @param orderId ID заказа.
     * @param newStatus Новый статус заказа.
     */
    suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        val order = orderDao.getOrderById(orderId)
        if (order != null) {
            orderDao.updateOrderStatus(orderId, newStatus)
        } else {
            throw IllegalArgumentException("Заказ с ID $orderId не найден.")
        }
    }

    @Transaction
    private suspend fun placeOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>): Long {
        val orderId = orderDao.insertOrder(order)
        val itemsWithOrderId = items.map { it.copy(orderId = orderId) }
        orderDao.insertOrderItems(itemsWithOrderId)
        return orderId
    }
}
