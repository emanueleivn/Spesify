package com.manele.spesify.features.auth.domain.model

import com.manele.spesify.core.domain.User

data class AuthSession(
    val sessionId: String,
    val user: User,
    val createdAtMillis: Long,
)