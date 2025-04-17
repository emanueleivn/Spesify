package com.manele.spesify.model

data class ShoppingList(
    val id: Long = 0,
    val title: String,
    val total: Double = 0.0,
    val products: List<Product> = emptyList()
)
