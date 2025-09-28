package com.manele.spesify.features.auth.presentation.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.manele.spesify.features.auth.domain.model.AuthSession
import com.manele.spesify.features.auth.presentation.login.LoginRoute
import com.manele.spesify.features.auth.presentation.login.LoginViewModel
import com.manele.spesify.features.auth.presentation.register.RegisterRoute
import com.manele.spesify.features.auth.presentation.register.RegisterViewModel

@Composable
fun AuthNavigation(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    onAuthenticated: (AuthSession) -> Unit,
    startDestination: AuthDestination = AuthDestination.Login,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
    ) {
        authGraph(
            navController = navController,
            loginViewModel = loginViewModel,
            registerViewModel = registerViewModel,
            onAuthenticated = onAuthenticated,
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    onAuthenticated: (AuthSession) -> Unit,
) {
    composable(AuthDestination.Login.route) {
        LoginRoute(
            viewModel = loginViewModel,
            onNavigateToRegister = {
                navController.navigate(AuthDestination.Register.route) {
                    launchSingleTop = true
                }
            },
            onAuthenticated = onAuthenticated,
        )
    }

    composable(AuthDestination.Register.route) {
        RegisterRoute(
            viewModel = registerViewModel,
            onNavigateToLogin = {
                val restored = navController.popBackStack(
                    route = AuthDestination.Login.route,
                    inclusive = false,
                )
                if (!restored) {
                    navController.navigate(AuthDestination.Login.route) {
                        launchSingleTop = true
                    }
                }
            },
            onAuthenticated = onAuthenticated,
        )
    }
}