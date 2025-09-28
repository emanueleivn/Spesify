package com.manele.spesify.features.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.manele.spesify.features.auth.domain.model.AuthSession

@Composable
fun RegisterRoute(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit,
    onAuthenticated: (AuthSession) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is RegisterEvent.Authenticated -> onAuthenticated(event.session)
            }
        }
    }

    RegisterScreen(
        state = uiState,
        onAction = viewModel::onAction,
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier,
    )
}

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onAction: (RegisterUiAction) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Crea un account",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.userName,
            onValueChange = { onAction(RegisterUiAction.UserNameChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nome utente") },
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { onAction(RegisterUiAction.EmailChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onAction(RegisterUiAction.PasswordChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { onAction(RegisterUiAction.ConfirmPasswordChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Conferma password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { onAction(RegisterUiAction.Submit) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Text("Registrati")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Hai già un account? Accedi")
        }
    }
}