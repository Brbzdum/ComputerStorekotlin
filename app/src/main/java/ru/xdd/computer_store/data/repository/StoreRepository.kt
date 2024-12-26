package ru.xdd.computer_store.data.repository

import android.content.SharedPreferences
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.xdd.computer_store.data.dao.*
import ru.xdd.computer_store.model.*
import javax.inject.Inject


class StoreRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val reviewDao: ReviewDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao
) {


    suspend fun getReviewsByProductId(productId: Long): List<ReviewEntity> {
        return reviewDao.getReviewsForProductFlow(productId).first()
    }



    // Пользователи
    suspend fun createUser(username: String, email: String, passwordHash: String, role: String = "USER") {
        try {
            val userRole = Role.valueOf(role.uppercase())
            userDao.insertUser(UserEntity(username = username, email = email, passwordHash = passwordHash, role = userRole))
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Неверная роль: $role")
        }
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return userDao.getUserByUsername(username)
            ?: throw IllegalArgumentException("Пользователь с таким именем не найден")
    }

    suspend fun deleteUserById(userId: Long) {
        userDao.deleteUserById(userId)
    }
    // В StoreRepository
    suspend fun getUserById(userId: Long): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun getOrdersByUserId(userId: Long): List<OrderEntity> {
        return orderDao.getOrdersByUserId(userId)
    }
    fun saveCurrentUser(userId: Long, role: String) {
        sharedPreferences.edit().apply {
            putLong("userId", userId)
            putString("role", role)
            apply()
        }
    }

    // Получение текущего пользователя
    fun getCurrentUser(): Pair<Long, String?> {
        val userId = sharedPreferences.getLong("userId", -1L)
        val role = sharedPreferences.getString("role", null)
        return userId to role
    }

    // Удаление данных пользователя (logout)
    fun logout() {
        sharedPreferences.edit().clear().apply()
    }



    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    // Товары
    suspend fun getProductById(productId: Long): ProductEntity? {
        return productDao.getProductById(productId)
    }


    suspend fun insertProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }

    // Управление аксессуарами
    suspend fun addAccessoryToProduct(productId: Long, accessoryId: Long) {
        if (productId == accessoryId) throw IllegalArgumentException("Продукт не может быть аксессуаром самого себя")
        productDao.addAccessoryToProduct(productId, accessoryId)
    }

    suspend fun removeAccessoryFromProduct(productId: Long, accessoryId: Long) {
        productDao.removeAccessoryFromProduct(productId, accessoryId)
    }

    // Потоки данных
    fun getAllProductsFlow(): Flow<List<ProductEntity>> = productDao.getAllProductsFlow()
    fun getMainProductsFlow(): Flow<List<ProductEntity>> = productDao.getMainProductsFlow()
    fun getAccessoriesForProductFlow(parentId: Long): Flow<List<ProductEntity>> = productDao.getAccessoriesForProductFlow(parentId)
    fun getReviewsForProductFlow(productId: Long): Flow<List<ReviewEntity>> = reviewDao.getReviewsForProductFlow(productId)
    fun getCartItemsForUserFlow(userId: Long): Flow<List<CartItemEntity>> = cartDao.getCartItemsForUserFlow(userId)
    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsersFlow()
    fun getOrdersForUserFlow(userId: Long): Flow<List<OrderEntity>> = orderDao.getOrdersForUserFlow(userId)

    // Работа с отзывами
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
        updateProductRating(productId) // Обновляем рейтинг после добавления отзыва
    }

    private suspend fun updateProductRating(productId: Long) {
        val reviews = reviewDao.getReviewsForProductFlow(productId).first()
        val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average().toFloat() else 0.0f
        productDao.updateRating(productId, averageRating)
    }

    suspend fun deleteReview(reviewId: Long) {
        reviewDao.deleteReview(reviewId)
        // Опционально: Обновить рейтинг после удаления отзыва
    }

    // Работа с корзиной
    suspend fun addToCart(userId: Long, productId: Long, quantity: Int) {
        val product = productDao.getProductById(productId) ?: throw IllegalArgumentException("Товар не найден")
        if (product.stock < quantity) throw IllegalArgumentException("Недостаточно товара на складе")

        val existingCartItems = cartDao.getCartItemsForUserFlow(userId).first()
        val existingCartItem = existingCartItems.find { it.productId == productId }

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

    suspend fun removeCartItemById(cartItemId: Long) {
        cartDao.deleteCartItemById(cartItemId)
    }

    suspend fun clearCartForUser(userId: Long) {
        cartDao.clearCartForUser(userId)
    }

    // Работа с заказами
    suspend fun placeOrder(userId: Long, items: List<Pair<ProductEntity, Int>>, shippingAddress: String): Long {
        // Проверка наличия товаров на складе
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
                orderId = 0, // будет установлен в транзакции placeOrderWithItems
                productId = it.first.productId,
                quantity = it.second,
                priceAtOrderTime = it.first.price
            )
        }
        val orderId = placeOrderWithItems(order, orderItems)

        // Обновление запасов на складе
        items.forEach { (product, quantity) ->
            val updatedProduct = product.copy(stock = product.stock - quantity)
            productDao.updateProduct(updatedProduct)
        }

        // Очистка корзины пользователя
        clearCartForUser(userId)

        return orderId
    }

    @Transaction
    private suspend fun placeOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>): Long {
        val orderId = orderDao.insertOrder(order)
        val itemsWithOrderId = items.map { it.copy(orderId = orderId) }
        orderDao.insertOrderItems(itemsWithOrderId)
        return orderId
    }

    // Управление аксессуарами
    // Методы уже объявлены выше, убедитесь, что они объявлены только один раз
    // fun addAccessoryToProduct и fun removeAccessoryFromProduct уже объявлены
    fun getProductWithAccessoriesFlow(productId: Long): Flow<ProductWithAccessories> {
        return productDao.getProductWithAccessoriesFlow(productId)
    }

    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity> = orderDao.getOrderItems(orderId)
    suspend fun hasCompletedOrderForProduct(userId: Long, productId: Long): Boolean {
        return orderDao.hasCompletedOrderForProduct(userId, productId)
    }

}
