package ru.xdd.computer_store.data.repository

import android.content.SharedPreferences
import androidx.room.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.xdd.computer_store.data.dao.*
import ru.xdd.computer_store.model.*
import ru.xdd.computer_store.utils.GUEST_USER_ID

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
    fun saveUser(userId: Long, role: Role) {
        sharedPreferences.edit().apply {
            putLong("user_id", userId)
            putString("user_role", role.toString()) // Сохраняем роль как строку
            apply()
        }
    }

    /**
     * Создает нового пользователя в базе данных.
     * @param username Имя пользователя.
     * @param email Электронная почта.
     * @param passwordHash Хэшированный пароль.
     * @param role Роль пользователя (по умолчанию `Role.USER`).
     * @return ID созданного пользователя.
     */
    suspend fun createUser(username: String, email: String, passwordHash: String, role: Role = Role.USER): Long {
        val user = UserEntity(
            username = username,
            email = email,
            passwordHash = passwordHash,
            role = role
        )
        return userDao.insertUser(user) // Возвращаем результат выполнения insertUser
    }




    /**
     * Получает текущего пользователя из SharedPreferences.
     * @return Пара `userId` и `role`, или `(-1, null)`, если пользователь не найден.
     */
    fun getUser(): Pair<Long, Role?> {
        val userId = sharedPreferences.getLong("user_id", -1L)
        val roleString = sharedPreferences.getString("user_role", null)
        val role = roleString?.let { Role.valueOf(it) } // Конвертируем строку обратно в Role
        return userId to role
    }


    /**
     * Удаляет данные текущего пользователя из SharedPreferences (выход из системы).
     */
    fun logoutUser() {
        sharedPreferences.edit().clear().apply()
    }
    /**
     * Возвращает пользователя по имени пользователя.
     * @param username Имя пользователя.
     * @return Экземпляр `UserEntity`, если пользователь найден, иначе `null`.
     */
    suspend fun getUserByUsername(username: String): UserEntity? {
        return userDao.getUserByUsername(username)
    }
    // --- Админские методы ---

    /**
     * Получает поток всех заказов для администратора.
     * @return Поток списка всех заказов.
     */
    fun getOrdersForAdminFlow(): Flow<List<OrderEntity>> {
        return orderDao.getAllOrdersFlow()
    }

    /**
     * Добавляет новый продукт в базу данных.
     * @param product Продукт для добавления.
     */
    suspend fun addProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    /**
     * Обновляет существующий продукт в базе данных.
     * @param product Продукт для обновления.
     */
    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    /**
     * Удаляет продукт из базы данных по его ID.
     * @param productId ID продукта для удаления.
     */
    suspend fun deleteProduct(productId: Long) {
        productDao.deleteProductById(productId)
    }
    // --- Методы для работы с Гостем ---

    /**
     * Добавляет товар в корзину гостя.
     * @param productId ID товара, который нужно добавить.
     * @param quantity Количество товара.
     */
    fun addGuestCartItem(productId: Long, quantity: Long) {
        val cartKey = "guest_cart" // Ключ для хранения корзины гостя в SharedPreferences

        // Получаем текущую корзину из SharedPreferences
        val cartJson = sharedPreferences.getString(cartKey, null)
        val cartItems: MutableMap<Long, Long> = if (cartJson != null) {
            // Если корзина уже существует, преобразуем JSON в Map
            Gson().fromJson(cartJson, object : TypeToken<MutableMap<Long, Long>>() {}.type)
        } else {
            // Если корзина отсутствует, создаём новую пустую Map
            mutableMapOf()
        }

        // Добавляем товар в корзину или обновляем его количество
        cartItems[productId] = (cartItems[productId] ?: 0) + quantity

        // Сохраняем обновлённую корзину обратно в SharedPreferences в виде JSON
        sharedPreferences.edit()
            .putString(cartKey, Gson().toJson(cartItems))
            .apply()
    }


    /**
     * Получает содержимое корзины гостя.
     * @return Map, где ключ — ID товара, а значение — его количество.
     */
    fun getGuestCartItems(): Map<Long, Long> {
        val cartKey = "guest_cart" // Ключ для получения корзины гостя из SharedPreferences

        // Получаем корзину гостя из SharedPreferences
        val cartJson = sharedPreferences.getString(cartKey, null)
        return if (cartJson != null) {
            // Если корзина существует, преобразуем JSON в Map
            Gson().fromJson(cartJson, object : TypeToken<Map<Long, Long>>() {}.type)
        } else {
            // Если корзина отсутствует, возвращаем пустую Map
            emptyMap()
        }
    }
    /**
     * Удаляет товар из корзины гостя.
     * @param productId ID товара, который нужно удалить.
     */
    fun removeGuestCartItem(productId: Long) {
        val cartKey = "guest_cart" // Ключ для хранения корзины гостя в SharedPreferences

        // Получаем текущую корзину из SharedPreferences
        val cartJson = sharedPreferences.getString(cartKey, null)
        val cartItems: MutableMap<Long, Long> = if (cartJson != null) {
            // Если корзина уже существует, преобразуем JSON в Map
            Gson().fromJson(cartJson, object : TypeToken<MutableMap<Long, Long>>() {}.type)
        } else {
            // Если корзина отсутствует, создаём новую пустую Map
            mutableMapOf()
        }

        // Удаляем товар из корзины
        cartItems.remove(productId)

        // Сохраняем обновлённую корзину обратно в SharedPreferences
        sharedPreferences.edit()
            .putString(cartKey, Gson().toJson(cartItems))
            .apply()
    }

    /**
     * Очищает корзину гостя.
     */
    fun clearGuestCart() {
        val cartKey = "guest_cart" // Ключ для удаления корзины гостя из SharedPreferences

        // Удаляем корзину гостя из SharedPreferences
        sharedPreferences.edit()
            .remove(cartKey)
            .apply()
    }
    fun updateGuestCartItems(cartItems: Map<Long, Long>) {
        val cartKey = "guest_cart"
        sharedPreferences.edit()
            .putString(cartKey, Gson().toJson(cartItems))
            .apply()
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

    /**
     * Возвращает поток продукта с аксессуарами.
     * @param productId ID товара.
     * @return Поток продукта с аксессуарами.
     */
    fun getProductWithAccessoriesFlow(productId: Long): Flow<ProductWithAccessories> =
        productDao.getProductWithAccessoriesFlow(productId)

    /**
     * Обновляет аксессуары для товара.
     * @param productId ID основного товара.
     * @param accessoryId ID аксессуара.
     * @param isAdding Флаг, добавлять или удалять аксессуар.
     */
    suspend fun updateProductAccessories(productId: Long, accessoryId: Long, isAdding: Boolean) {
        if (productId == accessoryId) throw IllegalArgumentException("Продукт не может быть аксессуаром самого себя")
        if (isAdding) {
            productDao.addAccessoryToProduct(productId, accessoryId)
        } else {
            productDao.removeAccessoryFromProduct(productId, accessoryId)
        }
    }

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
    suspend fun addProductToCart(userId: Long, productId: Long, quantity: Long = 1) {
        if (userId == GUEST_USER_ID) {
            // Обработка для гостя (например, отдельная таблица или логика в памяти)
            addGuestCartItem(productId, quantity)
        } else {
            // Стандартная логика для авторизованных пользователей
            val product = productDao.getProductById(productId)
                ?: throw IllegalArgumentException("Товар не найден")
            if (product.stock < quantity) throw IllegalArgumentException("Недостаточно товара на складе")

            val cartItem = cartDao.getCartItemByUserIdAndProductId(userId, productId)
            if (cartItem != null) {
                cartDao.updateCartItemQuantity(cartItem.cartItemId, cartItem.quantity + quantity)
            } else {
                cartDao.insertCartItem(
                    CartItemEntity(
                        userId = userId,
                        productId = productId,
                        quantity = quantity
                    )
                )
            }
        }
    }
    /**
     * Обновляет количество товара в корзине.
     * @param cartItemId ID элемента корзины.
     * @param quantity Новое количество.
     */
    suspend fun updateCartItemQuantity(cartItemId: Long, quantity: Long) {
        cartDao.updateCartItemQuantity(cartItemId, quantity)
    }


    /**
     * Удаляет товар из корзины по ID элемента корзины.
     * @param cartItemId ID элемента корзины.
     */
    suspend fun removeItemFromCart(cartItemId: Long) {
        cartDao.deleteCartItemById(cartItemId)
    }

    /**
     * Очищает корзину пользователя.
     * @param userId ID пользователя.
     */
    suspend fun clearUserCart(userId: Long) {
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
    suspend fun createOrder(
        userId: Long,
        items: List<Pair<ProductEntity, Int>>, // Список пар (товар, количество).
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

        clearUserCart(userId)

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

    /**
     * Вспомогательный метод для создания заказа и его элементов в рамках одной транзакции.
     * @param order Объект заказа.
     * @param items Список элементов заказа.
     * @return ID созданного заказа.
     */
    @Transaction
    private suspend fun placeOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>): Long {
        val orderId = orderDao.insertOrder(order)
        val itemsWithOrderId = items.map { it.copy(orderId = orderId) }
        orderDao.insertOrderItems(itemsWithOrderId)
        return orderId
    }
    /**
     * Возвращает поток заказов для пользователя.
     * @param userId ID пользователя.
     * @return Поток списка заказов.
     */
    fun getOrdersForUserFlow(userId: Long): Flow<List<OrderEntity>> {
        return orderDao.getOrdersForUserFlow(userId)
    }

}
