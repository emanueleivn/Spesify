package com.manele.spesify.repo

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ShoppingListEntity::class, ProductEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun productDao(): ProductDao
}
