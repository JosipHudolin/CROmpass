package com.example.crompass.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.crompass.R

data class ThemeState(
    val isDarkTheme: Boolean,
    val setDarkTheme: (Boolean) -> Unit
)

val LocalThemeState = compositionLocalOf<ThemeState> {
    error("No ThemeState provided")
}

val Rubik = FontFamily(
    Font(R.font.rubik_regular, FontWeight.Normal),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)

@Composable
fun CROmpassTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkThemeState = remember { mutableStateOf(useDarkTheme) }

    val themeState = remember {
        ThemeState(
            isDarkTheme = darkThemeState.value,
            setDarkTheme = { darkThemeState.value = it }
        )
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkThemeState.value) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkThemeState.value -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalThemeState provides themeState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}