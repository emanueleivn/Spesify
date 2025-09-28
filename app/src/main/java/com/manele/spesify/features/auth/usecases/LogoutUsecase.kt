package com.manele.spesify.features.auth.usecases
import com.manele.spesify.features.auth.domain.repo.AuthorizationRepository

class LogoutUseCase(
    private val repository: AuthorizationRepository,
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}