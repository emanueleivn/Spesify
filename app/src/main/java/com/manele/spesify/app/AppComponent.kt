package com.manele.spesify.app
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.manele.spesify.core.data.dao.UserDao
import com.manele.spesify.features.auth.data.FirestoreAuthorizationRepository
import com.manele.spesify.features.auth.usecases.LoginUseCase
import com.manele.spesify.features.auth.usecases.ObserveAuthSessionUseCase
import com.manele.spesify.features.auth.usecases.RegisterUserUseCase

/**
 * Very small service locator used to assemble the dependencies required by the
 * presentation layer.
 */
class AppComponent(context: Context) {

    private val firebaseComponent = FirebaseComponent(context)

    private val userDao by lazy { UserDao(firebaseComponent.firestore) }
    private val authorizationRepository by lazy { FirestoreAuthorizationRepository(userDao) }

    private val loginUseCase by lazy { LoginUseCase(authorizationRepository) }
    private val registerUserUseCase by lazy { RegisterUserUseCase(authorizationRepository) }
    private val observeAuthSessionUseCase by lazy { ObserveAuthSessionUseCase(authorizationRepository) }

    val mainViewModelFactory: ViewModelProvider.Factory by lazy {
        MainViewModelFactory(observeAuthSessionUseCase)
    }

    val loginViewModelFactory: ViewModelProvider.Factory by lazy {
        LoginViewModelFactory(loginUseCase, observeAuthSessionUseCase)
    }

    val registerViewModelFactory: ViewModelProvider.Factory by lazy {
        RegisterViewModelFactory(registerUserUseCase, observeAuthSessionUseCase)
    }
}