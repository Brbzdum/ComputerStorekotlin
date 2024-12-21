//package ru.xdd.computer_store.di
//
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import ru.xdd.computer_store.data.repository.StoreRepository
//import ru.xdd.computer_store.data.dao.*
//import ru.xdd.computer_store.model.AppDatabase
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object StoreRepositoryModule {
//
//    @Provides
//    @Singleton
//    fun provideStoreRepository(
//        userDao: UserDao,
//        productDao: ProductDao,
//        reviewDao: ReviewDao,
//        cartDao: CartDao,
//        orderDao: OrderDao
//    ): StoreRepository {
//        return StoreRepository(userDao, productDao, reviewDao, cartDao, orderDao)
//    }
//
//    @Provides
//    @Singleton
//    fun provideUserDao(db: AppDatabase): UserDao {
//        return db.userDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideProductDao(db: AppDatabase): ProductDao {
//        return db.productDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideReviewDao(db: AppDatabase): ReviewDao {
//        return db.reviewDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideCartDao(db: AppDatabase): CartDao {
//        return db.cartDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideOrderDao(db: AppDatabase): OrderDao {
//        return db.orderDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideAppDatabase(appContext: android.content.Context): AppDatabase {
//        return AppDatabase.getDatabase(appContext)
//    }
//}
