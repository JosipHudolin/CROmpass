package com.example.crompass.screen

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.viewmodel.EmergencyViewModel
import androidx.compose.foundation.shape.CircleShape
import com.example.crompass.viewmodel.UserViewModel
import androidx.core.net.toUri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.ui.res.stringResource
import com.example.crompass.R
import com.example.crompass.ui.theme.CroatianWhite
import com.example.crompass.utils.LocalAppLocale

@Composable
fun EmergencyScreen(
    navController: NavHostController,
    viewModel: EmergencyViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val contacts by viewModel.contacts.collectAsState()
    val tips by viewModel.tips.collectAsState()
    val language = LocalAppLocale.current.currentLanguageCode

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All") + tips.map { it.category }.distinct()
    val filteredTips = if (selectedCategory == "All") tips else tips.filter { it.category == selectedCategory }

    LaunchedEffect(Unit) {
        viewModel.getEmergencyData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White
                )
            }

            Text(
                text = stringResource(R.string.emergency_contacts),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.White
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(contacts) { contact ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = contact.translatedNames[language] ?: contact.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = contact.number, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.emergency_tips),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    var expanded by remember { mutableStateOf(false) }

                    Box {
                        OutlinedButton(onClick = { expanded = true }) {
                            Text(if (selectedCategory == "All") stringResource(R.string.all) else selectedCategory)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(text = if (category == "All") stringResource(R.string.all) else category) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(filteredTips) { tip ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = tip.category.uppercase(),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = tip.translations.getOrElse(language ?: "en") { stringResource(R.string.unknown) },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.find_nearby_help),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.White
                    )

                    val emergencyPlaces = mapOf(
                        stringResource(R.string.hospital) to "Bolnica",
                        stringResource(R.string.police) to "Policija",
                        stringResource(R.string.fire_department) to "Vatrogasci",
                        stringResource(R.string.pharmacy) to "Ljekarna"
                    )

                    emergencyPlaces.forEach { (englishLabel, croatianQuery) ->
                        TextButton(
                            onClick = {
                                val gmmIntentUri = "geo:0,0?q=$croatianQuery".toUri()
                                val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(stringResource(R.string.search_for) + " $englishLabel")
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                    data = "tel:112".toUri()
                }
                context.startActivity(intent)
            },
            containerColor = Color.Red,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp)
                .size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Emergency,
                contentDescription = null,
                tint = CroatianWhite,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
