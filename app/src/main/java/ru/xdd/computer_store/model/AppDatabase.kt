package ru.xdd.computer_store.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.dao.*
import ru.xdd.computer_store.utils.Converters
import ru.xdd.computer_store.utils.PasswordHasher


@Database(
    entities = [ProductEntity::class, UserEntity::class, CartItemEntity::class, OrderEntity::class, ReviewEntity::class,OrderItemEntity::class],
    version = 2,
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
                    .addCallback(DatabaseCallback(scope)) // Подключаем Callback
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    private class DatabaseCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    // Добавляем примерных пользователей
                    database.userDao().insertUser(
                        UserEntity(
                            username = "admin",
                            email = "admin@store.com",
                            passwordHash = PasswordHasher.hashPassword("admin123"),
                            role = Role.ADMIN
                        )
                    )
                    database.userDao().insertUser(
                        UserEntity(
                            username = "user1",
                            email = "user1@store.com",
                            passwordHash = PasswordHasher.hashPassword("user123"),
                            role = Role.USER
                        )
                    )

                    // Добавляем примерные продукты
                    database.productDao().insertProduct(
                        ProductEntity(
                            productId = 1,
                            name = "Ноутбук Lenovo",
                            description = "Высокопроизводительный ноутбук для работы и учебы.",
                            category = "Компьютеры",
                            price = 55000.0,
                            stock = 10, // Указываем количество на складе
                            rating = 4.5f, // Дополнительно можно указать рейтинг
                            reviews = listOf("Отличный ноутбук!", "Рекомендую к покупке"),
                            imageUrl = "https://p3-ofp.static.pub//fes/cms/2024/07/17/mhhtt47y7i849yv9ocqq4lwr6ly3h8024182.png" // Можно оставить пустым, если изображения нет
                        )
                    )

                    database.productDao().insertProduct(
                        ProductEntity(
                            productId = 2,
                            name = "Мышь Logitech",
                            description = "Эргономичная мышь с высокой точностью.",
                            category = "Аксессуары",
                            price = 1500.0,
                            stock = 50,
                            rating = 4.8f,
                            reviews = listOf("Удобная мышь", "Лучший выбор для офиса"),
                            imageUrl = "https://ir.ozone.ru/s3/multimedia-0/c1000/6350655876.jpg"
                        )
                    )

                    database.productDao().insertProduct(
                        ProductEntity(
                            productId = 3,
                            name = "Монитор Samsung",
                            description = "HD монитор с высоким качеством изображения.",
                            category = "Мониторы",
                            price = 12000.0,
                            stock = 20,
                            rating = 4.7f,
                            reviews = listOf("Четкое изображение", "Хорошая цветопередача"),
                            imageUrl = "https://3logic.ru/pimg/pim/regular/1428262.jpg"
                        )
                    )


                    // Добавляем примерный отзыв
                    database.reviewDao().insertReview(
                        ReviewEntity(
                            userId = 2, // ID user1
                            productId = 1, // ID ноутбука
                            rating = 5,
                            comment = "Отличный ноутбук!",
                            createdAt = System.currentTimeMillis()
                        )
                    )

                    // Добавляем примерный заказ
                    val orderId = database.orderDao().insertOrder(
                        OrderEntity(
                            userId = 2, // ID user1
                            orderDate = System.currentTimeMillis(),
                            orderStatus = OrderStatus.ЗАВЕРШЁН,
                            totalAmount = 55000.0,
                            shippingAddress = "Москва, Красная площадь, д.1"
                        )
                    )
                    database.orderDao().insertOrderItems(
                        listOf(
                            OrderItemEntity(
                                orderId = orderId,
                                productId = 1, // ID ноутбука
                                quantity = 1,
                                priceAtOrderTime = 55000.0
                            )
                        )
                    )
                }
            }
        }
    }


}
