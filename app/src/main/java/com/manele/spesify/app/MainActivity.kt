package com.manele.spesify.app;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.manele.spesify.app.AppComponent
import com.manele.spesify.app.MainViewModel
import com.manele.spesify.features.auth.domain.model.AuthSession
import com.manele.spesify.features.auth.presentation.login.LoginViewModel
import com.manele.spesify.features.auth.presentation.navigation.AuthNavigation
import com.manele.spesify.features.auth.presentation.register.RegisterViewModel

class MainActivity : ComponentActivity() {

    private val appComponent by lazy { AppComponent(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpesifyApp(appComponent = appComponent)
        }
    }
}

@Composable
private fun SpesifyApp(
    appComponent: AppComponent,
    modifier: Modifier = Modifier,
) {
    val mainViewModel: MainViewModel = viewModel(factory = appComponent.mainViewModelFactory)
    val state by mainViewModel.uiState.collectAsState()

    MaterialTheme {
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when {
                state.isLoading -> LoadingScreen()
                state.activeSession != null -> LoggedInScreen()
                else -> AuthFlow(appComponent = appComponent, onAuthenticated = mainViewModel::onAuthenticated)
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AuthFlow(
    appComponent: AppComponent,
    onAuthenticated: (AuthSession) -> Unit,
) {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel(factory = appComponent.loginViewModelFactory)
    val registerViewModel: RegisterViewModel = viewModel(factory = appComponent.registerViewModelFactory)

    AuthNavigation(
        navController = navController,
        loginViewModel = loginViewModel,
        registerViewModel = registerViewModel,
        onAuthenticated = onAuthenticated,
    )
}

@Composable
private fun LoggedInScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "sei loggato",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
    }
}