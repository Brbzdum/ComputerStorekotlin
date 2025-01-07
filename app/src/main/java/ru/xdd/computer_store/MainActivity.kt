package ru.xdd.computer_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.xdd.computer_store.model.AppDatabase
import ru.xdd.computer_store.model.OrderEntity
import ru.xdd.computer_store.model.OrderItemEntity
import ru.xdd.computer_store.model.OrderStatus
import ru.xdd.computer_store.model.ProductEntity
import ru.xdd.computer_store.model.ReviewEntity
import ru.xdd.computer_store.model.Role
import ru.xdd.computer_store.model.UserEntity
import ru.xdd.computer_store.ui.navigation.StoreNavGraph
import ru.xdd.computer_store.ui.theme.ComputerStoreTheme
import ru.xdd.computer_store.utils.PasswordHasher
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация базы данных
        initializeDatabase()

        setContent {
            ComputerStoreTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val userId = getUserIdFromPreferencesOrDefault()
                    val userRole = getUserRoleFromPreferencesOrDefault()

                    StoreNavGraph(
                        navController = navController,
                        userId = userId,
                        userRole = userRole
                    )
                }
            }
        }
    }

    private fun initializeDatabase() {
        lifecycleScope.launch {
            val productDao = database.productDao()
            val userDao = database.userDao()
            val reviewDao = database.reviewDao()
            val orderDao = database.orderDao()

            // Добавление пользователей
            if (userDao.getUserByUsername("admin") == null) {
                userDao.insertUser(
                    UserEntity(
                        username = "admin",
                        email = "admin@store.com",
                        passwordHash = PasswordHasher.hashPassword("admin123"),
                        role = Role.ADMIN
                    )
                )
            }

            if (userDao.getUserByUsername("user1") == null) {
                userDao.insertUser(
                    UserEntity(
                        username = "user1",
                        email = "user1@store.com",
                        passwordHash = PasswordHasher.hashPassword("user123"),
                        role = Role.USER
                    )
                )
            }

            // Проверка существования продуктов
            val existingProducts = productDao.getAllProductsFlow().first()
            if (existingProducts.isEmpty()) {
                val productId1 = productDao.insertProduct(
                    ProductEntity(
                        name = "Ноутбук Lenovo",
                        description = "Высокопроизводительный ноутбук для работы и учебы.",
                        category = "Компьютеры",
                        price = 55000.0,
                        stock = 10,
                        rating = 4.5f,
                        imageUrl = "https://p3-ofp.static.pub//fes/cms/2024/07/17/mhhtt47y7i849yv9ocqq4lwr6ly3h8024182.png",
                        parentProductId = null
                    )
                )
                val productId2 = productDao.insertProduct(
                    ProductEntity(
                        name = "Мышь Logitech",
                        description = "Эргономичная мышь с высокой точностью.",
                        category = "Аксессуары",
                        price = 1500.0,
                        stock = 50,
                        rating = 4.8f,
                        imageUrl = "https://ir.ozone.ru/s3/multimedia-0/c1000/6350655876.jpg",
                        parentProductId = null
                    )
                )
                val productId3 = productDao.insertProduct(
                    ProductEntity(
                        name = "Монитор Samsung",
                        description = "HD монитор с высоким качеством изображения.",
                        category = "Мониторы",
                        price = 12000.0,
                        stock = 20,
                        rating = 4.7f,
                        imageUrl = "https://3logic.ru/pimg/pim/regular/1428262.jpg",
                        parentProductId = null
                    )
                )

                productDao.addAccessoryToProduct(productId1, productId2)
                productDao.addAccessoryToProduct(productId1, productId3)

                val user1 = userDao.getUserByUsername("user1")
                user1?.let { user ->
                    reviewDao.insertReview(
                        ReviewEntity(
                            userId = user.userId,
                            productId = productId1,
                            rating = 5,
                            comment = "Отличный ноутбук!",
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }

                val orderId = orderDao.insertOrder(
                    OrderEntity(
                        userId = user1?.userId ?: 0,
                        orderDate = System.currentTimeMillis(),
                        orderStatus = OrderStatus.ЗАВЕРШЁН,
                        totalAmount = 55000.0,
                        shippingAddress = "Москва, Красная площадь, д.1"
                    )
                )
                orderDao.insertOrderItems(
                    listOf(
                        OrderItemEntity(
                            orderId = orderId,
                            productId = productId1,
                            quantity = 1,
                            priceAtOrderTime = 55000.0
                        )
                    )
                )
            }
        }
    }

    private fun getUserIdFromPreferencesOrDefault(): Long {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1L)
    }

    private fun getUserRoleFromPreferencesOrDefault(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("user_role", "USER") ?: "USER"
    }
}
