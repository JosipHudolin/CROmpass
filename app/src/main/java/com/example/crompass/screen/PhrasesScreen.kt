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
import androidx.compose.foundation.background
import androidx.compose.runtime.livedata.observeAsState
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.stringResource
import com.example.crompass.R
import com.example.crompass.screen.components.PhraseCard

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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            phrasesViewModel.getUserLanguage(userId)
        }
        phrasesViewModel.getPhrases()
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0), // ⬅️ uklanja automatski padding
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0), // ⬅️ ovo makne status bar padding
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = { Text(stringResource(R.string.common_phrases), color = MaterialTheme.colorScheme.secondary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = errorMessage ?: stringResource(R.string.unknown_error),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                ) {
                    items(phrases) { phrase ->
                        val phraseText = phrase.phrases[userLanguage] ?: phrase.phrases["hr"] ?: stringResource(R.string.unknown)
                        val croatianText = phrase.phrases["hr"] ?: stringResource(R.string.unknown_hr)

                        PhraseCard(
                            phraseText = phraseText,
                            translation = croatianText,
                            onClick = { speak(croatianText) }
                        )
                    }
                }
            }
        }
    }
}