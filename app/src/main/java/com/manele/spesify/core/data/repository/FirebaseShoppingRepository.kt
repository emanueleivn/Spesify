package com.manele.spesify.core.data.repository

import com.manele.spesify.core.data.dao.ProductDao
import com.manele.spesify.core.data.dao.ShoppingListDao
import com.manele.spesify.core.data.dao.UserDao
import com.manele.spesify.core.domain.Product
import com.manele.spesify.core.domain.ShoppingList
import com.manele.spesify.core.domain.User
import kotlinx.coroutines.flow.Flow

class FirebaseShoppingRepository(
    private val userDao: UserDao,
    private val shoppingListDao: ShoppingListDao,
    private val productDao: ProductDao,
) : ShoppingRepository {

    override fun observeUser(userId: String): Flow<User?> = userDao.observeUser(userId)

    override suspend fun getUser(userId: String): User? = userDao.getUser(userId)

    override suspend fun saveUser(user: User) {
        userDao.upsertUser(user)
    }

    override suspend fun deleteUser(userId: String) {
        userDao.deleteUser(userId)
    }

    override fun observeShoppingLists(userId: String): Flow<List<ShoppingList>> =
        shoppingListDao.observeShoppingLists(userId)

    override fun observeShoppingList(userId: String, listId: String): Flow<ShoppingList?> =
        shoppingListDao.observeShoppingList(userId, listId)

    override suspend fun getShoppingList(userId: String, listId: String): ShoppingList? =
        shoppingListDao.getShoppingList(userId, listId)

    override suspend fun saveShoppingList(userId: String, list: ShoppingList) {
        shoppingListDao.upsertShoppingList(userId, list)
    }

    override suspend fun deleteShoppingList(userId: String, listId: String) {
        shoppingListDao.deleteShoppingList(userId, listId)
    }

    override fun observeProducts(userId: String, listId: String): Flow<Map<Product, Int>> =
        productDao.observeProducts(userId, listId)

    override suspend fun saveProduct(
        userId: String,
        listId: String,
        product: Product,
        quantity: Int,
    ) {
        productDao.upsertProduct(userId, listId, product, quantity)
    }

    override suspend fun deleteProduct(userId: String, listId: String, product: Product) {
        productDao.deleteProduct(userId, listId, product)
    }
}
