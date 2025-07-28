package com.example.crompass.utils

import androidx.compose.runtime.compositionLocalOf
import java.util.*

data class AppLocaleController(
    val locale: Locale,
    val setLocale: (String) -> Unit
) {
    val currentLanguageCode: String
        get() = locale.language
}

val LocalAppLocale = compositionLocalOf<AppLocaleController> {
    error("No AppLocaleController provided")
}