package ru.xdd.computer_store.data.repository

import kotlinx.coroutines.flow.Flow
import ru.xdd.computer_store.data.dao.*
import ru.xdd.computer_store.model.*

class StoreRepository(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val reviewDao: ReviewDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao
) {


    // Пользователи
    suspend fun createUser(username: String, email: String, passwordHash: String, role: String = "user") {
        userDao.insertUser(UserEntity(username = username, email = email, passwordHash = passwordHash, role = role))
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return userDao.getUserByUsername(username)
    }



    suspend fun deleteUserById(userId: Long) {
        userDao.deleteUserById(userId)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }


    suspend fun getProductById(id: Long): ProductEntity? = productDao.getProductById(id)

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    fun getAllProductsFlow(): Flow<List<ProductEntity>> = productDao.getAllProductsFlow()
    fun getMainProductsFlow(): Flow<List<ProductEntity>> = productDao.getMainProductsFlow()
    fun getAccessoriesForProductFlow(parentId: Long): Flow<List<ProductEntity>> = productDao.getAccessoriesForProductFlow(parentId)
    fun getReviewsForProductFlow(productId: Long): Flow<List<ReviewEntity>> = reviewDao.getReviewsForProductFlow(productId)
    fun getCartItemsForUserFlow(userId: Long): Flow<List<CartItemEntity>> = cartDao.getCartItemsForUserFlow(userId)
    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsersFlow()
    fun getOrdersForUserFlow(userId: Long): Flow<List<OrderEntity>> = orderDao.getOrdersForUserFlow(userId)
    // Работа с отзывами
    suspend fun addReview(userId: Long, productId: Long, rating: Int, comment: String) {
        val review = ReviewEntity(
            userId = userId,
            productId = productId,
            rating = rating,
            comment = comment,
            createdAt = System.currentTimeMillis()
        )
        reviewDao.insertReview(review)
    }



    suspend fun deleteReview(reviewId: Long) {
        reviewDao.deleteReview(reviewId)
    }

    // Работа с корзиной
    suspend fun addToCart(userId: Long, productId: Long, quantity: Int) {
        val cartItem = CartItemEntity(
            userId = userId,
            productId = productId,
            quantity = quantity
        )
        cartDao.insertCartItem(cartItem)
    }



    suspend fun removeCartItemById(cartItemId: Long) {
        cartDao.deleteCartItemById(cartItemId)
    }

    suspend fun clearCartForUser(userId: Long) {
        cartDao.clearCartForUser(userId)
    }

    // Работа с заказами
    suspend fun placeOrder(userId: Long, items: List<Pair<ProductEntity, Int>>, shippingAddress: String): Long {
        val totalAmount = items.sumOf { it.first.price * it.second }
        val order = OrderEntity(
            userId = userId,
            orderDate = System.currentTimeMillis(),
            orderStatus = "NEW",
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
        return placeOrderWithItems(order, orderItems)
    }

    private suspend fun placeOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>): Long {
        val orderId = orderDao.insertOrder(order)
        val updatedItems = items.map { it.copy(orderId = orderId) }
        orderDao.insertOrderItems(updatedItems)
        return orderId
    }



    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity> = orderDao.getOrderItems(orderId)
}
