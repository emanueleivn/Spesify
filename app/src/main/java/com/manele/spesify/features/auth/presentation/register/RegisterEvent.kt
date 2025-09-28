package com.manele.spesify.features.auth.presentation.register

import com.manele.spesify.features.auth.domain.model.AuthSession

/**
 * One-off events emitted by [RegisterViewModel] that the UI should react to.
 */
sealed interface RegisterEvent {
    data class Authenticated(val session: AuthSession) : RegisterEvent
}