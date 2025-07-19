package com.example.crompass.screen

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun HomeScreen(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: ""

    Scaffold(
        bottomBar = {
            CROmpassBottomBar(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "🇬🇧",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable { /* TODO: language switch */ }
                    )
                    Text(
                        "Logout",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            Firebase.auth.signOut()
                            navController.navigate("auth") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }

                Text(
                    text = "Welcome to CROmpass!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButton("Phrases", Icons.Default.MailOutline, navController = navController)
                    HomeButton("Culture", Icons.Default.Person, navController = navController)
                    HomeButton("Translate", Icons.Default.Face, navController = navController)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeButton("Map", Icons.Default.Place, navController = navController, iconColor = MaterialTheme.colorScheme.tertiary)
                    HomeButton("Emergency", Icons.Default.Warning, navController = navController, iconColor = MaterialTheme.colorScheme.tertiary)
                    HomeButton("Reviews", Icons.Default.Star, navController = navController, iconColor = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}


@Composable
fun HomeButton(
    label: String,
    icon: ImageVector,
    navController: NavHostController,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                when (label) {
                    "Phrases" -> navController.navigate("phrases")
                    "Culture" -> { navController.navigate("culture") }
                    "Translate" -> { navController.navigate("translator") }
                    "Map" -> { navController.navigate("destination") }
                    "Emergency" -> { /* TODO */ }
                    "Reviews" -> { navController.navigate("review") }
                }
            }
            .padding(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(48.dp)
        )
        Text(text = label)
    }
}