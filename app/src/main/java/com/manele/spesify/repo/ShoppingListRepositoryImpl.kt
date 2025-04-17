package com.manele.spesify.repo

import com.manele.spesify.model.Product
import com.manele.spesify.model.ShoppingList

class ShoppingListRepositoryImpl(
    private val shoppingListDao: ShoppingListDao,
    private val productDao: ProductDao
) : ShoppingListRepository {

    override suspend fun getAllShoppingLists(): List<ShoppingList> {
        return shoppingListDao.getAll().map { ShoppingList(it.id, it.title, it.total) }
    }

    override suspend fun getShoppingListById(id: Long): ShoppingList? {
        return shoppingListDao.getById(id)?.let {
            val products = productDao.getByListId(it.id).map { productEntity ->
                Product(
                    id = productEntity.id,
                    name = productEntity.name,
                    quantity = productEntity.quantity,
                    unitPrice = productEntity.unitPrice,
                    isPurchased = productEntity.isPurchased
                )
            }
            ShoppingList(it.id, it.title, it.total, products)
        }
    }


    override suspend fun addShoppingList(list: ShoppingList) {
        shoppingListDao.insert(ShoppingListEntity(list.id, list.title, list.total))
    }

    override suspend fun updateShoppingList(list: ShoppingList) {
        val entity = ShoppingListEntity(
            id = list.id,
            title = list.title,
            total = list.products.sumOf { it.quantity * it.unitPrice }
        )
        shoppingListDao.insert(entity)
    }

    override suspend fun deleteShoppingList(id: Long) {
        shoppingListDao.getById(id)?.let { shoppingListDao.delete(it) }
    }

    override suspend fun addProductToList(listId: Long, product: Product) {
        productDao.insert(
            ProductEntity(
                id = product.id,
                listId = listId,
                name = product.name,
                quantity = product.quantity,
                unitPrice = product.unitPrice,
                isPurchased = product.isPurchased
            )
        )
        recalculateTotal(listId)
    }

    override suspend fun updateProductInList(listId: Long, product: Product) {
        val entity = ProductEntity(
            id = product.id,
            listId = listId,
            name = product.name,
            quantity = product.quantity,
            unitPrice = product.unitPrice,
            isPurchased = product.isPurchased
        )
        productDao.insert(entity)
        recalculateTotal(listId)
    }


    override suspend fun removeProductFromList(listId: Long, productId: Long) {
        productDao.getByListId(listId).firstOrNull { it.id == productId }?.let { productDao.delete(it) }
        recalculateTotal(listId)
    }

    private suspend fun recalculateTotal(listId: Long) {
        val products = productDao.getByListId(listId)
        val newTotal = products.sumOf { it.quantity * it.unitPrice }
        shoppingListDao.getById(listId)?.let { list ->
            shoppingListDao.insert(ShoppingListEntity(list.id, list.title, newTotal))
        }
    }
}
