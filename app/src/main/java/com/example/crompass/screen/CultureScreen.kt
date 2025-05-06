package com.example.crompass.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.viewmodel.CultureRulesViewModel

@Composable
fun CultureScreen(navController: NavHostController, viewModel: CultureRulesViewModel = viewModel()) {
    // Observe culture rules
    val cultureRules = viewModel.cultureRules

    Column(modifier = Modifier.padding(16.dp)) {
        if (cultureRules.isEmpty()) {
            Text(text = "No culture rules available", style = MaterialTheme.typography.bodyLarge)
        } else {
            cultureRules.forEach { rule ->
                Text(text = "Category: ${rule.category}")
                rule.translations.forEach { (language, translation) ->
                    Text(text = "$language: $translation")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}