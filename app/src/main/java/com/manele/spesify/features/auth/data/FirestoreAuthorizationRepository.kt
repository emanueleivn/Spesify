package com.manele.spesify.features.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.manele.spesify.core.data.dao.UserDao
import com.manele.spesify.core.domain.User
import com.manele.spesify.features.auth.domain.model.AuthCredentials
import com.manele.spesify.features.auth.domain.model.AuthResult
import com.manele.spesify.features.auth.domain.model.AuthSession
import com.manele.spesify.features.auth.domain.model.AuthorizationError
import com.manele.spesify.features.auth.domain.repo.AuthorizationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreAuthorizationRepository(
    private val auth: FirebaseAuth,
    private val userDao: UserDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AuthorizationRepository {

    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private val sessionState = MutableStateFlow<AuthSession?>(null)

    init {
        auth.currentUser?.let { firebaseUser ->
            scope.launch {
                val user = userDao.getUser(firebaseUser.uid)
                    ?: User(
                        id = firebaseUser.uid,
                        userName = firebaseUser.displayName ?: firebaseUser.email.orEmpty(),
                        email = firebaseUser.email.orEmpty(),
                    )
                sessionState.emit(createSession(user))
            }
        }
    }

    override fun observeAuthSession(): Flow<AuthSession?> = sessionState.asStateFlow()

    override suspend fun login(credentials: AuthCredentials): AuthResult =
        withContext(dispatcher) {
            try {
                val email = resolveEmail(credentials.identifier)
                    ?: return@withContext AuthResult.Failure(AuthorizationError.UserNotFound)

                val authResult = auth.signInWithEmailAndPassword(email, credentials.password).await()
                val firebaseUser = authResult.user
                    ?: return@withContext AuthResult.Failure(AuthorizationError.Unknown())

                val user = userDao.getUser(firebaseUser.uid)
                    ?: User(
                        id = firebaseUser.uid,
                        userName = firebaseUser.displayName ?: credentials.identifier,
                        email = firebaseUser.email ?: email,
                    ).also { userDao.upsertUser(it) }

                val session = createSession(user)
                sessionState.emit(session)
                AuthResult.Success(session)
            } catch (exception: FirebaseAuthInvalidUserException) {
                AuthResult.Failure(AuthorizationError.UserNotFound)
            } catch (exception: FirebaseAuthInvalidCredentialsException) {
                AuthResult.Failure(AuthorizationError.InvalidCredentials)
            } catch (throwable: Throwable) {
                AuthResult.Failure(AuthorizationError.Unknown(throwable))
            }
        }

    override suspend fun register(user: User, password: String): AuthResult =
        withContext(dispatcher) {
            try {
                val sanitizedUser = user.copy(
                    userName = user.userName.trim(),
                    email = user.email.trim(),
                )

                val existingAlias = userDao.findUserByUserName(sanitizedUser.userName)
                if (existingAlias != null) {
                    return@withContext AuthResult.Failure(AuthorizationError.UserAlreadyExists)
                }

                val authResult = auth
                    .createUserWithEmailAndPassword(sanitizedUser.email, password)
                    .await()
                val firebaseUser = authResult.user
                    ?: return@withContext AuthResult.Failure(AuthorizationError.Unknown())

                val profile = sanitizedUser.copy(id = firebaseUser.uid)
                userDao.upsertUser(profile)

                val session = createSession(profile)
                sessionState.emit(session)
                AuthResult.Success(session)
            } catch (exception: FirebaseAuthUserCollisionException) {
                AuthResult.Failure(AuthorizationError.UserAlreadyExists)
            } catch (exception: FirebaseAuthWeakPasswordException) {
                AuthResult.Failure(AuthorizationError.WeakPassword)
            } catch (exception: FirebaseAuthInvalidCredentialsException) {
                AuthResult.Failure(AuthorizationError.InvalidEmail)
            } catch (throwable: Throwable) {
                AuthResult.Failure(AuthorizationError.Unknown(throwable))
            }
        }

    override suspend fun logout() {
        withContext(dispatcher) {
            auth.signOut()
            sessionState.emit(null)
        }
    }

    private suspend fun resolveEmail(identifier: String): String? {
        val trimmed = identifier.trim()
        return if (trimmed.contains("@")) {
            trimmed
        } else {
            userDao.findUserByUserName(trimmed)?.email
        }
    }

    private fun createSession(user: User): AuthSession = AuthSession(
        sessionId = user.id,
        user = user,
        createdAtMillis = System.currentTimeMillis(),
    )
}