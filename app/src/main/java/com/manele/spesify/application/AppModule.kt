package com.manele.spesify.application

import android.content.Context
import androidx.room.Room
import com.manele.spesify.repo.AppDatabase
import com.manele.spesify.repo.ShoppingListRepository
import com.manele.spesify.repo.ShoppingListRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "shopping_list_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideShoppingListRepository(db: AppDatabase): ShoppingListRepository {
        return ShoppingListRepositoryImpl(db.shoppingListDao(), db.productDao())
    }
}
