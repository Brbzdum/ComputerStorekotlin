package ru.xdd.computer_store.model

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.dao.*
import ru.xdd.computer_store.utils.Converters
import ru.xdd.computer_store.utils.PasswordHasher

@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        ReviewEntity::class,
        OrderItemEntity::class,
        ProductAccessoryCrossRef::class
    ],
    version = 9 ,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "store.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("DatabaseCallback", "Database created")
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    try {
                        initializeData(database)
                        Log.d("DatabaseCallback", "Initial data inserted successfully")
                    } catch (e: Exception) {
                        Log.e("DatabaseCallback", "Error initializing database: ${e.message}", e)
                    }
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d("DatabaseCallback", "Database opened")
        }

        private suspend fun initializeData(database: AppDatabase) {
            val userDao = database.userDao()
            val productDao = database.productDao()
            val reviewDao = database.reviewDao()
            val orderDao = database.orderDao()

            Log.d("DatabaseCallback", "Starting data initialization")

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
                        imageUrl = "https://static.razer.ru/230822/800x600-razer-blackwidow-v3-pro-product-promo.png",
                        parentProductId = null
                    )
                )

                val product5 = productDao.insertProduct(
                    ProductEntity(
                        name = "SSD Kingston",
                        description = "Надежный SSD накопитель для вашего ПК.",
                        category = "Накопители",
                        price = 3000.0,
                        stock = 40,
                        rating = 4.9f,
                        imageUrl = "https://goughlui.com/wp-content/uploads/2020/08/2020072910537461.jpg",
                        parentProductId = null
                    )
                )

                val product6 = productDao.insertProduct(
                    ProductEntity(
                        name = "Наушники Sony",
                        description = "Качественные наушники с шумоподавлением.",
                        category = "Аудио",
                        price = 7000.0,
                        stock = 15,
                        rating = 4.7f,
                        imageUrl = "https://www.sony.ru/image/bcef63d32b0270480b5371fb35cd3029?fmt=png-alpha&wid=660&hei=660",
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


}
