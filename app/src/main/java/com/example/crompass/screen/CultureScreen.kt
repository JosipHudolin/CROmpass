package com.example.crompass.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.viewmodel.CultureRulesViewModel

@Composable
fun CultureScreen(navController: NavHostController, viewModel: CultureRulesViewModel = viewModel()) {
    val languageCodeToName = mapOf(
        "en" to "English",
        "de" to "German",
        "fr" to "French",
        "hr" to "Croatian",
        "it" to "Italian",
        "pl" to "Polish"
    )
    val cultureRules = viewModel.cultureRules
    val userLanguage by viewModel.userLanguage.observeAsState("en")
    val selectedLanguage by viewModel.selectedLanguage.observeAsState(userLanguage)
    val selectedCategory by viewModel.selectedCategory.observeAsState("All")
    val isLoading by viewModel.isLoading.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
                    .padding(end = 8.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "Cultural Rules",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Explore cultural etiquette and behavior tips.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val availableLanguages = listOf("en", "de", "fr", "hr", "it", "pl")
        val availableCategories = listOf("All") + cultureRules.map { it.category.replace("_", " ").replaceFirstChar { c -> c.uppercase() } }.distinct()

        SimpleDropdown(
            label = "Language",
            options = availableLanguages.map { languageCodeToName[it] ?: it },
            selectedOption = languageCodeToName[selectedLanguage] ?: selectedLanguage,
            onOptionSelected = { selectedName ->
                val selectedCode = languageCodeToName.entries.firstOrNull { it.value == selectedName }?.key ?: "en"
                viewModel.setSelectedLanguage(selectedCode)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SimpleDropdown(
            label = "Category",
            options = availableCategories,
            selectedOption = selectedCategory,
            onOptionSelected = { viewModel.setSelectedCategory(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text(
                text = "Loading cultural rules...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else if (cultureRules.isEmpty()) {
            Text(
                text = "No culture rules available.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            val filteredRules = viewModel.getFilteredCultureRules()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredRules) { rule ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                            Text(
                                text = rule.category.replace("_", " ").replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            rule.translations[selectedLanguage]?.let { translation ->
                                Text(
                                    text = translation,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}