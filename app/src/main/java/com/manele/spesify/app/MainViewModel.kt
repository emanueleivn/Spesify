package com.manele.spesify.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manele.spesify.features.auth.domain.model.AuthSession
import com.manele.spesify.features.auth.usecases.ObserveAuthSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Root [ViewModel] observing the authorization status of the current user.
 */
class MainViewModel(
    private val observeAuthSessionUseCase: ObserveAuthSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeAuthSessionUseCase().collect { session ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        activeSession = session,
                    )
                }
            }
        }
    }

    fun onAuthenticated(session: AuthSession) {
        _uiState.update {
            it.copy(
                isLoading = false,
                activeSession = session,
            )
        }
    }
}

/**
 * UI state exposed by [MainViewModel].
 */
data class MainUiState(
    val isLoading: Boolean = true,
    val activeSession: AuthSession? = null,
)