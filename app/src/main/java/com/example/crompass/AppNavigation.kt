package com.example.crompass

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.crompass.screen.AuthScreen
import com.example.crompass.screen.MainScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(navController: NavHostController, onLanguageChange: (String) -> Unit) {
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "main" else "auth"
    ) {
        composable("auth") { AuthScreen(navController) }
        composable("main") { MainScreen(navController, onLanguageChange) }
    }
}