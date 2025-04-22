package com.example.crompass

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Scaffold(
        bottomBar = { CROmpassBottomBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // ✅ Top-right language switch and Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
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
                        onLogout()
                    }
                )
            }

            // ✅ Centered content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to CROmpass!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        HomeButton("Phrases", Icons.Default.MailOutline)
                        HomeButton("Culture", Icons.Default.Person)
                        HomeButton("Translate", Icons.Default.Face)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        HomeButton("Map", Icons.Default.Place, MaterialTheme.colorScheme.tertiary)
                        HomeButton("Emergency", Icons.Default.Warning, MaterialTheme.colorScheme.tertiary)
                        HomeButton("Favorites", Icons.Default.Star, MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
    }
}


@Composable
fun HomeButton(label: String, icon: ImageVector, iconColor: Color = MaterialTheme.colorScheme.primary) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { /* TODO */ }
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

@Composable
fun CROmpassBottomBar() {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = true,
            onClick = { /* TODO */ },
            icon = {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = MaterialTheme.colorScheme.primary)
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            icon = {
                Icon(Icons.Default.Search, contentDescription = "Explore", tint = MaterialTheme.colorScheme.primary)
            },
            label = { Text("Explore") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            icon = {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
            },
            label = { Text("Settings") }
        )
    }
}