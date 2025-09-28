package com.manele.spesify.features.auth.domain.repo

import com.manele.spesify.core.domain.User
import com.manele.spesify.features.auth.domain.model.AuthCredentials
import com.manele.spesify.features.auth.domain.model.AuthResult
import com.manele.spesify.features.auth.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction that exposes authorization capabilities to the rest of the app.
 */
interface AuthorizationRepository {

    /**
     * Emits the current authenticated session (if any) and all its subsequent
     * updates.
     */
    fun observeAuthSession(): Flow<AuthSession?>

    /**
     * Attempts to authenticate the user with the provided [credentials].
     */
    suspend fun login(credentials: AuthCredentials): AuthResult

    /**
     * Attempts to register a new [user].
     */
    suspend fun register(user: User): AuthResult

    /**
     * Clears the current authorization session.
     */
    suspend fun logout()
}