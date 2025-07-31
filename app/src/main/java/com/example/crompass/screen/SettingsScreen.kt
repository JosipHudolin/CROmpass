package com.example.crompass.screen

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.stringResource
import com.example.crompass.R
import com.example.crompass.ui.theme.LocalThemeState
import com.example.crompass.utils.LocalAppLocale

@Composable
fun SettingsScreen(
    navController: NavHostController,
    globalNavController: NavHostController
) {
    val appLocale = LocalAppLocale.current
    val user = FirebaseAuth.getInstance().currentUser
    val currentLocale = LocalAppLocale.current
    var selectedLanguage by remember { mutableStateOf(currentLocale.currentLanguageCode) }
    var languageDropdownExpanded by remember { mutableStateOf(false) }

    val languages = mapOf(
        "English 🇬🇧" to "en",
        "Français 🇫🇷" to "fr",
        "Deutsch 🇩🇪" to "de",
        "Italiano 🇮🇹" to "it",
        "Polski 🇵🇱" to "pl",
        "Hrvatski 🇭🇷" to "hr"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(stringResource(id = R.string.settings), style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Language Section
        Column {
            Text(stringResource(id = R.string.language), style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = languages.entries.find { it.value == selectedLanguage }?.key ?: "English 🇬🇧",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { languageDropdownExpanded = true }) {
                    Text(stringResource(id = R.string.change_language))
                }
            }
            DropdownMenu(
                expanded = languageDropdownExpanded,
                onDismissRequest = { languageDropdownExpanded = false }
            ) {
                languages.forEach { (label, code) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedLanguage = code
                            languageDropdownExpanded = false
                            appLocale.setLocale(code)
                        }
                    )
                }
            }
        }

        // Theme Selection Section
        Column {
            Text(stringResource(id = R.string.theme), style = MaterialTheme.typography.titleMedium)
            val themeState = LocalThemeState.current

            val themeOptions = listOf(
                    Triple(R.string.light, false, false),
                    Triple(R.string.dark, true, false),
                    Triple(R.string.system_default, false, true)
            )

            themeOptions.forEach { (labelRes, darkValue, isSystem) ->
                val label = stringResource(id = labelRes)

                val isSelected = when {
                    isSystem -> themeState.useSystemTheme
                    darkValue -> !themeState.useSystemTheme && themeState.isDarkTheme
                    else -> !themeState.useSystemTheme && !themeState.isDarkTheme
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            themeState.setUseSystemTheme(isSystem)
                            if (!isSystem) themeState.setDarkTheme(darkValue)
                        }
                    )
                    Text(
                        text = label,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // User Info Display
        Column {
            Text(stringResource(id = R.string.user), style = MaterialTheme.typography.titleMedium)
            Text(user?.email ?: "Not logged in", style = MaterialTheme.typography.bodyMedium)
        }

        // Logout Button
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                globalNavController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
            }
        ) {
            Text(stringResource(id = R.string.logout))
        }
    }
}