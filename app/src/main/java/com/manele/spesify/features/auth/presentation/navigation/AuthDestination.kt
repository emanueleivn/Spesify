package com.manele.spesify.features.auth.presentation.navigation

sealed class AuthDestination(val route: String) {
    object Login : AuthDestination("auth/login")
    object Register : AuthDestination("auth/register")
}