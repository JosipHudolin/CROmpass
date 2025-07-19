package com.example.crompass

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crompass.screen.AuthScreen
import com.example.crompass.screen.CultureScreen
import com.example.crompass.screen.DestinationScreen
import com.example.crompass.screen.HomeScreen
import com.example.crompass.screen.PhrasesScreen
import com.example.crompass.screen.ProfileScreen
import com.example.crompass.screen.ReviewScreen
import com.example.crompass.screen.TranslatorScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val isAuthenticated = FirebaseAuth.getInstance().currentUser != null

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
            ProfileScreen(navController = navController)
        }

        composable("phrases") {
            PhrasesScreen(navController = navController)
        }

        composable("culture") {
            CultureScreen(navController = navController)
        }

        composable("translator") {
            TranslatorScreen(navController = navController)
        }
        composable("destination") {
            DestinationScreen(navController = navController)
        }
        composable("review") {
            ReviewScreen(navController = navController)
        }
    }
}
