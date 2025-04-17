package com.manele.spesify.model

data class Product(
    val id: Long = 0,
    val name: String,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val isPurchased: Boolean = false
) {
    fun totalPrice(): Double = quantity * unitPrice
}
