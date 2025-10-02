package com.manele.spesify.features.auth.usecases

import com.manele.spesify.core.domain.User
import com.manele.spesify.features.auth.domain.model.AuthResult
import com.manele.spesify.features.auth.domain.repo.AuthorizationRepository

class RegisterUserUseCase(
    private val repository: AuthorizationRepository,
) {
    suspend operator fun invoke(user: User, password: String): AuthResult =
        repository.register(user, password)
}
