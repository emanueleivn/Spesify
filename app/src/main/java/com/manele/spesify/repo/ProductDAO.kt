package com.manele.spesify.repo

import androidx.room.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE listId = :listId")
    suspend fun getByListId(listId: Long): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)
}
