package com.manele.spesify.repo

import com.manele.spesify.model.Product
import com.manele.spesify.model.ShoppingList

interface ShoppingListRepository {
    suspend fun getAllShoppingLists(): List<ShoppingList>
    suspend fun getShoppingListById(id: Long): ShoppingList?
    suspend fun addShoppingList(list: ShoppingList)
    suspend fun updateShoppingList(list: ShoppingList)
    suspend fun deleteShoppingList(id: Long)

    suspend fun addProductToList(listId: Long, product: Product)
    suspend fun updateProductInList(listId: Long, product: Product)
    suspend fun removeProductFromList(listId: Long, productId: Long)
}
