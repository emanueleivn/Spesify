package com.manele.spesify.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manele.spesify.features.auth.domain.model.AuthCredentials
import com.manele.spesify.features.auth.domain.model.AuthResult
import com.manele.spesify.features.auth.domain.model.AuthSession
import com.manele.spesify.features.auth.usecases.LoginUseCase
import com.manele.spesify.features.auth.usecases.ObserveAuthSessionUseCase
import com.manele.spesify.features.auth.presentation.mapper.toUiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    observeAuthSessionUseCase: ObserveAuthSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            observeAuthSessionUseCase().collect(::onSessionUpdated)
        }
    }

    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.IdentifierChanged -> _uiState.update {
                it.copy(identifier = action.value, errorMessage = null)
            }

            is LoginUiAction.PasswordChanged -> _uiState.update {
                it.copy(password = action.value, errorMessage = null)
            }

            LoginUiAction.Submit -> login()
        }
    }

    private fun login() {
        val identifier = _uiState.value.identifier.trim()
        val password = _uiState.value.password
        if (identifier.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = EMPTY_FIELDS_ERROR) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = loginUseCase(AuthCredentials(identifier, password))) {
                is AuthResult.Success -> _uiState.update { current ->
                    current.copy(isLoading = false, errorMessage = null)
                }

                is AuthResult.Failure -> _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = result.error.toUiMessage(),
                    )
                }
            }
        }
    }

    private suspend fun onSessionUpdated(session: AuthSession?) {
        _uiState.update { it.copy(activeSession = session) }
        if (session != null) {
            _events.emit(LoginEvent.Authenticated(session))
        }
    }

    private companion object {
        const val EMPTY_FIELDS_ERROR = "Inserisci un identificativo e una password."
    }
}