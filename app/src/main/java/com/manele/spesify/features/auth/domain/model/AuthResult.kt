package com.manele.spesify.features.auth.domain.model

sealed class AuthResult {
    data class Success(val session: AuthSession) : AuthResult()

    data class Failure(val error: AuthorizationError) : AuthResult()
}