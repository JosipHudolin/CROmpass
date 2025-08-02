package com.example.crompass.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import com.example.crompass.R
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import com.example.crompass.screen.components.CultureRuleCard
import com.example.crompass.screen.components.Dropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CultureScreen(navController: NavHostController, viewModel: CultureRulesViewModel = viewModel()) {
    val languageCodeToName = mapOf(
        "en" to stringResource(R.string.english),
        "de" to stringResource(R.string.german),
        "fr" to stringResource(R.string.french),
        "hr" to stringResource(R.string.croatian),
        "it" to stringResource(R.string.italian),
        "pl" to stringResource(R.string.polish),
    )
    val cultureRules = viewModel.cultureRules
    val userLanguage by viewModel.userLanguage.observeAsState("en")
    val selectedLanguage by viewModel.selectedLanguage.observeAsState(userLanguage)
    val selectedCategory by viewModel.selectedCategory.observeAsState("All")
    val isLoading by viewModel.isLoading.observeAsState(false)

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0), // ⬅️ uklanja automatski padding
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = stringResource(R.string.cultural_rules),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable { navController.popBackStack() }
                    )
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.explore_culture),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            val availableLanguages = listOf("en", "de", "fr", "hr", "it", "pl")
            val availableCategories = listOf("All") + cultureRules.map { it.category.replace("_", " ").replaceFirstChar { c -> c.uppercase() } }.distinct()

            Dropdown(
                label = stringResource(R.string.language),
                options = availableLanguages.map { languageCodeToName[it] ?: it },
                selectedOption = languageCodeToName[selectedLanguage] ?: selectedLanguage,
                onOptionSelected = { selectedName ->
                    val selectedCode = languageCodeToName.entries.firstOrNull { it.value == selectedName }?.key ?: "en"
                    viewModel.setSelectedLanguage(selectedCode)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Dropdown(
                label = stringResource(R.string.select_category),
                options = availableCategories,
                selectedOption = selectedCategory,
                onOptionSelected = { viewModel.setSelectedCategory(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Text(
                    text = stringResource(R.string.loading_culture_rules),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else if (cultureRules.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_culture_rules),
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
                        CultureRuleCard(
                            category = rule.category,
                            translation = rule.translations[selectedLanguage] ?: ""
                        )
                    }
                }
            }
        }
    }
}