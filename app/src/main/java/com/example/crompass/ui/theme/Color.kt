package com.example.crompass.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme


val CroatianRed = Color(0xFFC62828)
val CroatianBlue = Color(0xFF0B5394)
val CroatianLightRed = Color(0xFFEF5350) // svjetlija i življa crvena
val CroatianLightBlue = Color(0xFF42A5F5) // svjetlija plava
val CroatianWhite = Color(0xFFFFFFFF)
val CroatianGray = Color(0xFFF5F5F5)
val CroatianDarkGray = Color(0xFFB0BEC5)
val CroatianBlack = Color(0xFF212121)
val CroatianGold = Color(0xFFFFD700)
val CroatianBordo = Color(0xFF8B0000)

val LightColorScheme = lightColorScheme(
    primary = CroatianRed,
    secondary = CroatianBlue,
    background = CroatianWhite,
    surface = CroatianGray,
    onPrimary = CroatianWhite,
    onSecondary = CroatianWhite,
    onBackground = CroatianBlack,
    onSurface = CroatianBlack,
)

val DarkColorScheme = darkColorScheme(
    primary = CroatianLightRed,
    secondary = CroatianLightBlue,
    background = CroatianBlack,
    surface = CroatianDarkGray,
    onPrimary = CroatianWhite,
    onSecondary = CroatianWhite,
    onBackground = CroatianWhite,
    onSurface = CroatianWhite,
)