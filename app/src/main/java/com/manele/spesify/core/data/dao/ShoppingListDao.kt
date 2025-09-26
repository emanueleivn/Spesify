package com.manele.spesify.core.data.dao
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.manele.spesify.core.domain.Product
import com.manele.spesify.core.domain.ShoppingList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ShoppingListDao(
    private val firestore: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun observeShoppingLists(userId: String): Flow<List<ShoppingList>> = callbackFlow {
        val registration = listCollection(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val lists = snapshot?.documents?.mapNotNull { document ->
                document.toObject(ShoppingListDocument::class.java)?.toDomain()
            } ?: emptyList()
            trySend(lists).isSuccess
        }
        awaitClose { registration.remove() }
    }.flowOn(dispatcher)

    fun observeShoppingList(userId: String, listId: String): Flow<ShoppingList?> = callbackFlow {
        val registration = listCollection(userId).document(listId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val shoppingList = snapshot?.toObject(ShoppingListDocument::class.java)?.toDomain()
                trySend(shoppingList).isSuccess
            }
        awaitClose { registration.remove() }
    }.flowOn(dispatcher)

    suspend fun getShoppingList(userId: String, listId: String): ShoppingList? =
        withContext(dispatcher) {
            val snapshot = listCollection(userId).document(listId).get().await()
            snapshot.toObject(ShoppingListDocument::class.java)?.toDomain()
        }

    suspend fun upsertShoppingList(userId: String, list: ShoppingList) {
        withContext(dispatcher) {
            listCollection(userId)
                .document(list.title)
                .set(list.toDocument())
                .await()
        }
    }

    suspend fun deleteShoppingList(userId: String, listId: String) {
        withContext(dispatcher) {
            listCollection(userId).document(listId).delete().await()
        }
    }

    internal suspend fun updateProducts(
        userId: String,
        listId: String,
        products: Map<Product, Int>,
    ) {
        withContext(dispatcher) {
            val snapshot = listCollection(userId).document(listId).get().await()
            val existingTitle = snapshot.toObject(ShoppingListDocument::class.java)?.title ?: listId
            listCollection(userId)
                .document(listId)
                .set(ShoppingListDocument(existingTitle, products.toProductEntries()))
                .await()
        }
    }

    private fun listCollection(userId: String): CollectionReference =
        firestore.collection(USERS_COLLECTION).document(userId).collection(LISTS_COLLECTION)

    private fun ShoppingList.toDocument(): ShoppingListDocument =
        ShoppingListDocument(title = title, products = products.toProductEntries())

    private fun Map<Product, Int>.toProductEntries(): List<ProductEntry> =
        entries.map { (product, quantity) ->
            ProductEntry(
                name = product.name,
                unitPrice = product.unitPrice,
                quantity = quantity,
            )
        }

    private data class ShoppingListDocument(
        val title: String = "",
        val products: List<ProductEntry> = emptyList(),
    ) {
        fun toDomain(): ShoppingList = ShoppingList(
            title = title,
            products = products.associate { it.toDomainPair() },
        )
    }

    private data class ProductEntry(
        val name: String = "",
        val unitPrice: Double = 0.0,
        val quantity: Int = 0,
    ) {
        fun toDomainPair(): Pair<Product, Int> = Product(name, unitPrice) to quantity
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val LISTS_COLLECTION = "lists"
    }
}
