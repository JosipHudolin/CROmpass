package com.example.crompass.screen

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.viewmodel.TranslatorViewModel
import com.example.crompass.model.TranslationResult
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(navController: NavHostController, viewModel: TranslatorViewModel = viewModel()) {
    val context = LocalContext.current
    val translationResult by viewModel.translationResult.observeAsState()
    val recentTranslations by viewModel.recentTranslations.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    var inputText by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("hr") }

    val languageNames = mapOf(
        "en" to "English",
        "de" to "German",
        "fr" to "French",
        "it" to "Italian",
        "hr" to "Croatian",
        "pl" to "Polish"
    )

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(selectedLanguage) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale(selectedLanguage)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Translator") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter text") },
                modifier = Modifier.fillMaxWidth()
            )

            SimpleDropdown(
                label = "Target Language",
                options = languageNames.values.toList(),
                selectedOption = languageNames[selectedLanguage] ?: "English",
                onOptionSelected = { selectedName ->
                    selectedLanguage = languageNames.entries.firstOrNull { it.value == selectedName }?.key ?: "en"
                }
            )

            Button(
                onClick = { viewModel.translateText(inputText, selectedLanguage) },
                enabled = inputText.isNotBlank()
            ) {
                Text("Translate")
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
            }

            translationResult?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Translated: ${it.translatedText}")
                Button(onClick = {
                    tts?.language = Locale(selectedLanguage)
                    tts?.speak(it.translatedText, TextToSpeech.QUEUE_FLUSH, null, null)
                }) {
                    Text("Speak")
                }
            }

            if (recentTranslations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Recent Translations", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(recentTranslations) { item ->
                        Text(
                            text = "${item.originalText} → ${item.translatedText} (${item.targetLanguageCode})",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
