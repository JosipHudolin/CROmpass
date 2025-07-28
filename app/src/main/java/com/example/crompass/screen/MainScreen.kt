package com.example.crompass.screen


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainScreen(navController: NavHostController) {
    // Internal NavController for bottom bar/tab navigation
    val innerNavController = rememberNavController()
    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: ""

    Scaffold(
        bottomBar = {
            CROmpassBottomBar(
                navController = innerNavController,
                currentRoute = currentRoute
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("profile") { ProfileScreen(innerNavController) }
            composable("home") { HomeScreen(navController = innerNavController, rootNavController = navController) }
            composable("phrases") { PhrasesScreen(innerNavController) }
            composable("culture") { CultureScreen(innerNavController) }
            composable("translator") { TranslatorScreen(innerNavController) }
            composable("destination") {
                DestinationScreen(navController = innerNavController)
            }
            composable("emergency") { EmergencyScreen(innerNavController) }
            composable("review") { ReviewScreen(innerNavController) }
            // Use the global navController for settings to allow logout/app-level navigation
            composable("settings") {
                SettingsScreen(
                    navController = innerNavController,
                    globalNavController = navController
                )
            }
        }
    }
}