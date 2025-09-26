package com.manele.spesify.core.data.dao

import com.manele.spesify.core.domain.Product
import com.manele.spesify.core.domain.ShoppingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductDao(private val shoppingLists: Flow<ShoppingList>) {

    fun observeProducts(): Flow<Map<Product, Int>> =
        shoppingLists.map { it.products }
}
