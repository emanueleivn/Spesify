package com.manele.spesify.features.auth.usecases

import com.manele.spesify.features.auth.domain.model.AuthSession
import com.manele.spesify.features.auth.domain.repo.AuthorizationRepository
import kotlinx.coroutines.flow.Flow

class ObserveAuthSessionUseCase(
    private val repository: AuthorizationRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = repository.observeAuthSession()
}