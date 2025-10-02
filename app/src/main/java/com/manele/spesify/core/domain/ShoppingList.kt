package com.manele.spesify.core.domain

data class ShoppingList(
    val id: String,
    val title: String,
    val products: Map<Product, Int> = emptyMap(),
) {
    fun total(): Double {
        return products.entries.sumOf { (product, quantity) ->
            product.unitPrice * quantity
        }
    }
}