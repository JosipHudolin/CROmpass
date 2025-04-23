package com.example.crompass

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val isAuthenticated = Firebase.auth.currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "auth"
    ) {
        composable("auth") {
            AuthScreen(navController = navController)
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )
        }
    }
}
