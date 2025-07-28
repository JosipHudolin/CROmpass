package com.example.crompass.utils


import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

data class ThemeState(
    val isDarkTheme: Boolean?,
    val setDarkTheme: (Boolean) -> Unit
)

val LocalThemeState = compositionLocalOf {
    ThemeState(
        isDarkTheme = null,
        setDarkTheme = {}
    )
}