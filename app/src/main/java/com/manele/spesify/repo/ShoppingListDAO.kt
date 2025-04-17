package com.manele.spesify.repo

import androidx.room.*

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_lists")
    suspend fun getAll(): List<ShoppingListEntity>

    @Query("SELECT * FROM shopping_lists WHERE id = :id")
    suspend fun getById(id: Long): ShoppingListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: ShoppingListEntity)

    @Delete
    suspend fun delete(list: ShoppingListEntity)
}
