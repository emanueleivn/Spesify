package com.manele.spesify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = FreshGreenPrimary,
    onPrimary = FreshOnPrimary,
    primaryContainer = FreshGreenPrimary,

    secondary = FreshTomatoRed,
    secondaryContainer = FreshCarrotOrange,
    onSecondary = FreshOnSecondary,

    background = FreshBackground,
    onBackground = FreshOnBackground,

    surface = FreshSurface,
    onSurface = FreshOnSurface,

    error = FreshError,
    onError = FreshOnError
)

private val DarkColorScheme = darkColorScheme(
    primary = FreshGreenPrimary,
    onPrimary = FreshOnPrimary,
    primaryContainer = FreshGreenPrimary,

    secondary = FreshTomatoRed,
    secondaryContainer = FreshCarrotOrange,
    onSecondary = FreshOnSecondary,

    background = FreshBackground,
    onBackground = FreshOnBackground,

    surface = FreshSurface,
    onSurface = FreshOnSurface,

    error = FreshError,
    onError = FreshOnError
)

@Composable
fun SpesifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
