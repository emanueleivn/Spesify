package com.manele.spesify.features.auth.data

import com.manele.spesify.core.data.dao.UserDao
import com.manele.spesify.core.domain.User
import com.manele.spesify.features.auth.domain.model.AuthCredentials
import com.manele.spesify.features.auth.domain.model.AuthResult
import com.manele.spesify.features.auth.domain.model.AuthSession
import com.manele.spesify.features.auth.domain.model.AuthorizationError
import com.manele.spesify.features.auth.domain.repo.AuthorizationRepository
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class FirestoreAuthorizationRepository(
    private val userDao: UserDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AuthorizationRepository {

    private val sessionState = MutableStateFlow<AuthSession?>(null)

    override fun observeAuthSession(): Flow<AuthSession?> = sessionState.asStateFlow()

    override suspend fun login(credentials: AuthCredentials): AuthResult =
        withContext(dispatcher) {
            try {
                val user = userDao.getUser(credentials.identifier)
                    ?: return@withContext AuthResult.Failure(AuthorizationError.UserNotFound)

                if (user.password != credentials.password) {
                    return@withContext AuthResult.Failure(AuthorizationError.InvalidCredentials)
                }

                val session = createSession(user)
                sessionState.emit(session)
                AuthResult.Success(session)
            } catch (throwable: Throwable) {
                AuthResult.Failure(AuthorizationError.Unknown(throwable))
            }
        }

    override suspend fun register(user: User): AuthResult = withContext(dispatcher) {
        try {
            val existing = userDao.getUser(user.userName)
            if (existing != null) {
                return@withContext AuthResult.Failure(AuthorizationError.UserAlreadyExists)
            }

            userDao.upsertUser(user)
            val session = createSession(user)
            sessionState.emit(session)
            AuthResult.Success(session)
        } catch (throwable: Throwable) {
            AuthResult.Failure(AuthorizationError.Unknown(throwable))
        }
    }

    override suspend fun logout() {
        sessionState.emit(null)
    }

    private fun createSession(user: User): AuthSession = AuthSession(
        sessionId = UUID.randomUUID().toString(),
        user = user,
        createdAtMillis = System.currentTimeMillis(),
    )
}