package com.manele.spesify.core.data.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.manele.spesify.core.domain.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserDao(
    private val firestore: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun observeUser(userId: String): Flow<User?> = callbackFlow {
        val registration = usersCollection().document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(UserDocument::class.java)?.toDomain()
                trySend(user).isSuccess
            }
        awaitClose { registration.remove() }
    }.flowOn(dispatcher)

    suspend fun getUser(userId: String): User? = withContext(dispatcher) {
        val snapshot = usersCollection().document(userId).get().await()
        snapshot.toObject(UserDocument::class.java)?.toDomain()
    }

    suspend fun upsertUser(user: User) {
        withContext(dispatcher) {
            usersCollection().document(user.userName)
                .set(user.toDocument())
                .await()
        }
    }

    suspend fun deleteUser(userId: String) {
        withContext(dispatcher) {
            usersCollection().document(userId).delete().await()
        }
    }

    private fun usersCollection() = firestore.collection(USERS_COLLECTION)

    private fun User.toDocument(): UserDocument = UserDocument(
        userName = userName,
        email = email,
        password = password,
    )

    private data class UserDocument(
        val userName: String = "",
        val email: String = "",
        val password: String = "",
    ) {
        fun toDomain(): User = User(
            userName = userName,
            email = email,
            password = password,
        )
    }

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}
