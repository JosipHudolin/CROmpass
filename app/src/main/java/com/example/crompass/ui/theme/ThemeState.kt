package com.example.crompass.ui.theme

data class ThemeState(
    val isDarkTheme: Boolean,
    val useSystemTheme: Boolean,
    val setDarkTheme: (Boolean) -> Unit,
    val setUseSystemTheme: (Boolean) -> Unit
)
