package com.manele.spesify.core.data.repository

import com.manele.spesify.core.domain.Product
import com.manele.spesify.core.domain.ShoppingList
import com.manele.spesify.core.domain.User
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {

    fun observeUser(userId: String): Flow<User?>

    suspend fun getUser(userId: String): User?

    suspend fun saveUser(user: User)

    suspend fun deleteUser(userId: String)

    fun observeShoppingLists(userId: String): Flow<List<ShoppingList>>

    fun observeShoppingList(userId: String, listId: String): Flow<ShoppingList?>

    suspend fun getShoppingList(userId: String, listId: String): ShoppingList?

    suspend fun saveShoppingList(userId: String, list: ShoppingList)

    suspend fun deleteShoppingList(userId: String, listId: String)

    fun observeProducts(userId: String, listId: String): Flow<Map<Product, Int>>

    suspend fun saveProduct(userId: String, listId: String, product: Product, quantity: Int)

    suspend fun deleteProduct(userId: String, listId: String, product: Product)
}
