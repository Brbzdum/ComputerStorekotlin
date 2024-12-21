package ru.xdd.computer_store.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.AppDatabase
import ru.xdd.computer_store.data.dao.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideStoreRepository(db: AppDatabase): StoreRepository {
        return StoreRepository(
            userDao = db.userDao(),
            productDao = db.productDao(),
            reviewDao = db.reviewDao(),
            cartDao = db.cartDao(),
            orderDao = db.orderDao()
        )
    }
}

