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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "store.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    database.userDao().insertUser(
                        UserEntity(
                            username = "admin",
                            email = "admin@store.com",
                            passwordHash = PasswordHasher.hashPassword("admin123"),
                            role = "admin"
                        )
                    )
                }
            }
        }
    }

}
