package com.example.crompass.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme


val CroatianRed = Color(0xFFC62828)
val CroatianBlue = Color(0xFF0B5394)
val CroatianWhite = Color(0xFFFFFFFF)
val CroatianGray = Color(0xFFF5F5F5)
val CroatianDarkGray = Color(0xFFB0BEC5)
val CroatianBlack = Color(0xFF212121)
val CroatianGold = Color(0xFFFFD700)
val CroatianBordo = Color(0xFF8B0000)
val CroatianLightBlue = Color(0xFFADD8E6)

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
    primary = CroatianRed,
    secondary = CroatianBlue,
    background = CroatianBlack,
    surface = CroatianDarkGray,
    onPrimary = CroatianBlack,
    onSecondary = CroatianBlack,
    onBackground = CroatianWhite,
    onSurface = CroatianWhite,
)