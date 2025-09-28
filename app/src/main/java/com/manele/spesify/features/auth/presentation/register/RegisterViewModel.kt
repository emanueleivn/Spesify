package com.manele.spesify.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manele.spesify.core.domain.User
import com.manele.spesify.features.auth.domain.model.AuthResult
import com.manele.spesify.features.auth.domain.model.AuthSession
import com.manele.spesify.features.auth.usecases.ObserveAuthSessionUseCase
import com.manele.spesify.features.auth.usecases.RegisterUserUseCase
import com.manele.spesify.features.auth.presentation.mapper.toUiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    observeAuthSessionUseCase: ObserveAuthSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegisterEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<RegisterEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            observeAuthSessionUseCase().collect(::onSessionUpdated)
        }
    }

    fun onAction(action: RegisterUiAction) {
        when (action) {
            is RegisterUiAction.UserNameChanged -> _uiState.update {
                it.copy(userName = action.value, errorMessage = null)
            }

            is RegisterUiAction.EmailChanged -> _uiState.update {
                it.copy(email = action.value, errorMessage = null)
            }

            is RegisterUiAction.PasswordChanged -> _uiState.update {
                it.copy(password = action.value, errorMessage = null)
            }

            is RegisterUiAction.ConfirmPasswordChanged -> _uiState.update {
                it.copy(confirmPassword = action.value, errorMessage = null)
            }

            RegisterUiAction.Submit -> register()
        }
    }

    private fun register() {
        val state = _uiState.value
        if (state.userName.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = EMPTY_FIELDS_ERROR) }
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = PASSWORD_MISMATCH_ERROR) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = registerUserUseCase(
                User(
                    userName = state.userName.trim(),
                    email = state.email.trim(),
                    password = state.password,
                ),
            )) {
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
            _events.emit(RegisterEvent.Authenticated(session))
        }
    }

    private companion object {
        const val EMPTY_FIELDS_ERROR = "Compila tutti i campi per procedere."
        const val PASSWORD_MISMATCH_ERROR = "Le password non coincidono."
    }
}