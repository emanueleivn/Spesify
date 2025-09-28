package com.manele.spesify.features.auth.presentation.login

import com.manele.spesify.features.auth.domain.model.AuthSession

data class LoginUiState(
    val identifier: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeSession: AuthSession? = null,
)