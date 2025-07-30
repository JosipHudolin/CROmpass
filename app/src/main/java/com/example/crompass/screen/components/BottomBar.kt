package com.example.crompass.screen.components

import androidx.compose.ui.res.stringResource
import com.example.crompass.R

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.crompass.ui.theme.CroatianGold
import com.example.crompass.ui.theme.CroatianRed
import com.example.crompass.ui.theme.CroatianWhite

@Composable
fun CROmpassBottomBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = CroatianRed) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = stringResource(R.string.home),
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.home),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = CroatianWhite, // mala pozadina kad je selected
                selectedIconColor = CroatianGold,
                unselectedIconColor = CroatianWhite,
                selectedTextColor = CroatianGold,
                unselectedTextColor = CroatianWhite
            )
        )
        NavigationBarItem(
            selected = currentRoute == "destination",
            onClick = { navController.navigate("destination") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = stringResource(R.string.explore),
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.explore),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = CroatianWhite, // mala pozadina kad je selected
                selectedIconColor = CroatianGold,
                unselectedIconColor = CroatianWhite,
                selectedTextColor = CroatianGold,
                unselectedTextColor = CroatianWhite
            )
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.PermIdentity,
                    contentDescription = stringResource(R.string.profile),
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.profile),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = CroatianWhite, // mala pozadina kad je selected
                selectedIconColor = CroatianGold,
                unselectedIconColor = CroatianWhite,
                selectedTextColor = CroatianGold,
                unselectedTextColor = CroatianWhite
            )
        )
    }
}