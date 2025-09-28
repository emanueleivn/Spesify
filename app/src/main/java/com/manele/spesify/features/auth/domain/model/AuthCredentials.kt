package com.manele.spesify.features.auth.domain.model

data class AuthCredentials(
    val identifier: String,
    val password: String,
)