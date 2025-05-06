package com.example.crompass.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.viewmodel.PhraseViewModel
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.livedata.observeAsState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasesScreen(navController: NavHostController) {
    val phrasesViewModel: PhraseViewModel = viewModel()

    // Observe LiveData from the ViewModel
    val phrases by phrasesViewModel.phrases.observeAsState(emptyList())
    val isLoading by phrasesViewModel.isLoading.observeAsState(false)
    val errorMessage by phrasesViewModel.errorMessage.observeAsState("")
    val userLanguage by phrasesViewModel.userLanguage.observeAsState("en") // Observe user language

    // Initialize Text-to-Speech
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageAvailable = tts?.isLanguageAvailable(Locale("hr")) == TextToSpeech.LANG_AVAILABLE
                tts?.language = if (languageAvailable) Locale("hr") else Locale("en")
            }
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Load phrases data on screen load
    LaunchedEffect(Unit) {
        phrasesViewModel.fetchPhrases()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Common Phrases") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            CROmpassBottomBar(navController, currentRoute = "phrases")
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(phrases) { phrase ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val phraseText = phrase.phrases["hr"] ?: "Nepoznato"
                                    speak(phraseText)  // Speak in Croatian
                                },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                // Render the phrase in the selected language (German, Croatian, etc.)
                                Text(
                                    text = when (userLanguage) {
                                        "de" -> phrase.phrases["de"] ?: phrase.phrases["hr"] ?: "Unknown"
                                        "hr" -> phrase.phrases["hr"] ?: phrase.phrases["de"] ?: "Unknown"
                                        else -> phrase.phrases["en"] ?: "Unknown"
                                    },
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(4.dp))

                                // Always show Croatian translation
                                Text(
                                    text = "➡️ ${phrase.phrases["hr"] ?: "Nepoznato"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}