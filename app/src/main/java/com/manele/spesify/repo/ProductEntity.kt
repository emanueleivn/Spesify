package com.manele.spesify.repo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val name: String,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val isPurchased: Boolean = false
)
