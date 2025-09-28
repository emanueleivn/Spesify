package com.manele.spesify.features.auth.presentation.register

sealed interface RegisterUiAction {
    data class UserNameChanged(val value: String) : RegisterUiAction
    data class EmailChanged(val value: String) : RegisterUiAction
    data class PasswordChanged(val value: String) : RegisterUiAction
    data class ConfirmPasswordChanged(val value: String) : RegisterUiAction
    object Submit : RegisterUiAction
}