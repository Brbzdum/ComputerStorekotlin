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
                val product1 = productDao.insertProduct(
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

                val product2 = productDao.insertProduct(
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

                val product3 = productDao.insertProduct(
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

                // Новые товары
                val product4 = productDao.insertProduct(
                    ProductEntity(
                        name = "Клавиатура Razer",
                        description = "Игровая клавиатура с подсветкой RGB.",
                        category = "Аксессуары",
                        price = 4500.0,
                        stock = 30,
                        rating = 4.6f,
                        imageUrl = "https://static.razer.ru/public/zNkw6qaHWUwkskJHn2GcsN/800x600-razer-deathstalker-v2-pro-product-promo.png",
                        parentProductId = null
                    )
                )

                val product5 = productDao.insertProduct(
                    ProductEntity(
                        name = "SSD Kingston",
                        description = "Надежный SSD накопитель для вашего ПК.",
                        category = "Аксессуары",
                        price = 3000.0,
                        stock = 40,
                        rating = 4.9f,
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRgY8y9bi57wGbP1kguRA_IUwYTaDw9jv75XQ&s",
                        parentProductId = null
                    )
                )

                val product6 = productDao.insertProduct(
                    ProductEntity(
                        name = "Наушники Sony",
                        description = "Качественные наушники с шумоподавлением.",
                        category = "Аксессуары",
                        price = 7000.0,
                        stock = 15,
                        rating = 4.7f,
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdNxBWSAzTJcIBOrm7iL69S_gZmD-P13vJbw&s",
                        parentProductId = null
                    )
                )

                // Добавляем аксессуары к продуктам
                productDao.addAccessoryToProduct(product1, product2)
                productDao.addAccessoryToProduct(product1, product3)
                productDao.addAccessoryToProduct(product3, product4)

                // Отзывы
                val user1 = userDao.getUserByUsername("user1")
                user1?.let { user ->
                    reviewDao.insertReview(
                        ReviewEntity(
                            userId = user.userId,
                            productId = product1,
                            rating = 5,
                            comment = "Отличный ноутбук!",
                            createdAt = System.currentTimeMillis()
                        )
                    )
                    reviewDao.insertReview(
                        ReviewEntity(
                            userId = user.userId,
                            productId = product6,
                            rating = 4,
                            comment = "Хорошие наушники, но могли бы быть дешевле.",
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }

                // Пример заказа
                val orderId = orderDao.insertOrder(
                    OrderEntity(
                        userId = user1?.userId ?: 0,
                        orderDate = System.currentTimeMillis(),
                        orderStatus = OrderStatus.ЗАВЕРШЁН,
                        totalAmount = 62000.0,
                        shippingAddress = "Москва, Красная площадь, д.1"
                    )
                )
                orderDao.insertOrderItems(
                    listOf(
                        OrderItemEntity(
                            orderId = orderId,
                            productId = product1,
                            quantity = 1,
                            priceAtOrderTime = 55000.0
                        ),
                        OrderItemEntity(
                            orderId = orderId,
                            productId = product2,
                            quantity = 2,
                            priceAtOrderTime = 1500.0
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
