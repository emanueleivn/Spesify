package com.manele.spesify.features.auth.presentation.login

import com.manele.spesify.features.auth.domain.model.AuthSession

/**
 * One-off events emitted by [LoginViewModel] that the UI should react to.
 */
sealed interface LoginEvent {
    data class Authenticated(val session: AuthSession) : LoginEvent
}