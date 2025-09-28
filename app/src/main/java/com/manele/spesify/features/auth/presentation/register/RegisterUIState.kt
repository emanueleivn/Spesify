package com.manele.spesify.features.auth.presentation.register

import com.manele.spesify.features.auth.domain.model.AuthSession

data class RegisterUiState(
    val userName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeSession: AuthSession? = null,
)