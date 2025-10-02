package com.manele.spesify.features.auth.presentation.mapper

import com.manele.spesify.features.auth.domain.model.AuthorizationError

fun AuthorizationError.toUiMessage(): String = when (this) {
    AuthorizationError.UserNotFound -> "Utente non trovato."
    AuthorizationError.InvalidCredentials -> "Le credenziali inserite non sono corrette."
    AuthorizationError.InvalidEmail -> "L'indirizzo email non è valido."
    AuthorizationError.WeakPassword -> "La password è troppo debole. Scegline una più sicura."
    AuthorizationError.UserAlreadyExists -> "Esiste già un account con queste informazioni."
    is AuthorizationError.Unknown -> throwable?.localizedMessage
        ?: "Si è verificato un errore inatteso. Riprova più tardi."
}