package com.example.crompass

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun CROmpassBottomBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = MaterialTheme.colorScheme.primary) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "explore",
            onClick = { navController.navigate("explore") },
            icon = { Icon(Icons.Default.Search, contentDescription = "Explore", tint = MaterialTheme.colorScheme.primary) },
            label = { Text("Explore") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = MaterialTheme.colorScheme.primary) },
            label = { Text("Profile") }
        )
    }
}