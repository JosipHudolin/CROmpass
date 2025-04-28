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
import androidx.navigation.NavHostController
import com.example.crompass.model.Phrase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import android.speech.tts.TextToSpeech
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasesScreen(navController: NavHostController) {
    var phrases by remember { mutableStateOf<List<Phrase>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userLanguage by remember { mutableStateOf("en") }

    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Provjeri dostupnost hrvatskog jezika
                val languageAvailable = tts?.isLanguageAvailable(Locale("hr")) == TextToSpeech.LANG_AVAILABLE
                if (languageAvailable) {
                    tts?.language = Locale("hr") // Ako je hrvatski jezik dostupan
                } else {
                    tts?.language = Locale("en") // Ako nije, koristi engleski
                }
            } else {
                errorMessage = "Failed to initialize Text-to-Speech"
            }
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    LaunchedEffect(Unit) {
        val auth = Firebase.auth
        val db = Firebase.firestore

        val userId = auth.currentUser?.uid
        if (userId != null) {
            try {
                val document = db.collection("users").document(userId).get().await()
                userLanguage = document.getString("language") ?: "en"

                val result = db.collection("phrases").get().await()
                val fetchedPhrases = result.map { it.toObject(Phrase::class.java) }
                phrases = fetchedPhrases
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        } else {
            errorMessage = "User not logged in"
            isLoading = false
        }
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
                                    speak(phraseText)  // Izgovara na hrvatskom
                                },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = phrase.phrases[userLanguage]
                                        ?: phrase.phrases["en"]
                                        ?: "Unknown",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(4.dp))
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