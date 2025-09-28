package com.manele.spesify.features.auth.presentation.login

sealed interface LoginUiAction {
    data class IdentifierChanged(val value: String) : LoginUiAction
    data class PasswordChanged(val value: String) : LoginUiAction
    object Submit : LoginUiAction
}