package com.manele.spesify.features.auth.usecases

import com.manele.spesify.features.auth.domain.model.AuthCredentials
import com.manele.spesify.features.auth.domain.model.AuthResult
import com.manele.spesify.features.auth.domain.repo.AuthorizationRepository

class LoginUseCase(
    private val repository: AuthorizationRepository,
) {
    suspend operator fun invoke(credentials: AuthCredentials): AuthResult =
        repository.login(credentials)
}