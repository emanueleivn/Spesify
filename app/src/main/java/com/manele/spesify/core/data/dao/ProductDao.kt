package com.manele.spesify.core.data.dao

import com.manele.spesify.core.domain.Product
import com.manele.spesify.core.domain.ShoppingList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext

class ProductDao(
    private val shoppingListDao: ShoppingListDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun observeProducts(userId: String, listId: String): Flow<Map<Product, Int>> =
        shoppingListDao.observeShoppingList(userId, listId)
            .filterNotNull()
            .map {it.products}

    suspend fun upsertProduct(
        userId: String,
        listId: String,
        product: Product,
        quantity: Int,
    ) {
        withContext(dispatcher) {
            val currentList = shoppingListDao.getShoppingList(userId, listId)
                ?: ShoppingList(id = listId, title = listId)
            val updatedProducts = currentList.products.toMutableMap().apply {
                put(product, quantity)
            }
            shoppingListDao.updateProducts(userId, listId, updatedProducts)
        }
    }

    suspend fun deleteProduct(
        userId: String,
        listId: String,
        product: Product,
    ) {
        withContext(dispatcher) {
            val currentList = shoppingListDao.getShoppingList(userId, listId) ?: return@withContext
            val updatedProducts = currentList.products.toMutableMap().apply {
                remove(product)
            }
            shoppingListDao.updateProducts(userId, listId, updatedProducts)
        }
    }
}
