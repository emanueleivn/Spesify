package com.manele.spesify.features.auth.domain.model

sealed interface AuthorizationError {
    /** The provided identifier does not match any stored user. */
    object UserNotFound : AuthorizationError

    /** The provided credentials do not match the stored password. */
    object InvalidCredentials : AuthorizationError

    /** A user with the same identifier already exists. */
    object UserAlreadyExists : AuthorizationError

    /** Any other unexpected failure. */
    data class Unknown(val throwable: Throwable? = null) : AuthorizationError
}