package com.manele.spesify.app

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Entry point for Firebase related dependencies.
 *
 * This component ensures that the Firebase SDK is initialised before any
 * Firestore operation is executed and exposes a single [FirebaseFirestore]
 * instance shared across the app.
 */
class FirebaseComponent(private val context: Context) {

    /** Lazily created [FirebaseFirestore] instance backed by the default Firebase app. */
    val firestore: FirebaseFirestore by lazy {
        ensureFirebaseInitialized()
        FirebaseFirestore.getInstance()
    }

    init {
        ensureFirebaseInitialized()
    }

    private fun ensureFirebaseInitialized() {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
    }
}