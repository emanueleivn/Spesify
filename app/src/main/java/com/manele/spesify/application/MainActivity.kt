package com.manele.spesify.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.manele.spesify.R
import com.manele.spesify.ui.theme.SpesifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Spesify)
        val database = AppModule.provideDatabase(applicationContext)
        val repository = AppModule.provideShoppingListRepository(database)

        setContent {
            SpesifyTheme {
                AppNavigation(repository)
            }
        }
    }
}
