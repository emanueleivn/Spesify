package com.manele.spesify.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.manele.spesify.features.auth.usecases.LoginUseCase
import com.manele.spesify.features.auth.usecases.ObserveAuthSessionUseCase
import com.manele.spesify.features.auth.usecases.RegisterUserUseCase
import com.manele.spesify.features.auth.presentation.login.LoginViewModel
import com.manele.spesify.features.auth.presentation.register.RegisterViewModel

internal class MainViewModelFactory(
    private val observeAuthSessionUseCase: ObserveAuthSessionUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(observeAuthSessionUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${'$'}modelClass")
    }
}

internal class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val observeAuthSessionUseCase: ObserveAuthSessionUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginUseCase, observeAuthSessionUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${'$'}modelClass")
    }
}

internal class RegisterViewModelFactory(
    private val registerUserUseCase: RegisterUserUseCase,
    private val observeAuthSessionUseCase: ObserveAuthSessionUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(registerUserUseCase, observeAuthSessionUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${'$'}modelClass")
    }
}
