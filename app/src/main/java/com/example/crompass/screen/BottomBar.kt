package com.example.crompass.screen

import androidx.compose.ui.res.stringResource
import com.example.crompass.R

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun CROmpassBottomBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(R.string.home),
                    modifier = androidx.compose.ui.Modifier.size(32.dp),
                    tint = if (currentRoute == "home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = { Text(stringResource(R.string.home)) },
            alwaysShowLabel = false
        )
        NavigationBarItem(
            selected = currentRoute == "destination",
            onClick = { navController.navigate("destination") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = stringResource(R.string.explore),
                    modifier = androidx.compose.ui.Modifier.size(32.dp),
                    tint = if (currentRoute == "destination") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = { Text(stringResource(R.string.explore)) },
            alwaysShowLabel = false
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.profile),
                    modifier = androidx.compose.ui.Modifier.size(32.dp),
                    tint = if (currentRoute == "profile") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = { Text(stringResource(R.string.profile)) },
            alwaysShowLabel = false
        )
    }
}