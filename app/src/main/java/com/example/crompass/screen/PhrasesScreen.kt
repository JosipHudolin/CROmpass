package com.example.crompass.screen

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
import com.example.crompass.model.Phrase
import com.example.crompass.screen.components.Dropdown
import com.example.crompass.screen.components.PhraseCard
import com.example.crompass.utils.LocalAppLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhrasesScreen(navController: NavHostController) {
    val phrasesViewModel: PhraseViewModel = viewModel()

    // Observe LiveData from the ViewModel
    val phrases by phrasesViewModel.phrases.observeAsState(emptyList())
    val isLoading by phrasesViewModel.isLoading.observeAsState(false)
    val errorMessage by phrasesViewModel.errorMessage.observeAsState(null)
    val appLocale = LocalAppLocale.current
    // Always use appLocale.currentLanguageCode as the selected language
    val selectedLanguage = appLocale.currentLanguageCode

    // Initialize Text-to-Speech
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageAvailable = tts?.isLanguageAvailable(Locale("hr")) == TextToSpeech.LANG_AVAILABLE
                if (languageAvailable) {
                    tts?.language = Locale("hr")
                } else {
                    tts?.language = Locale("en")
                    tts?.speak("Language not supported", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }
    DisposableEffect(tts) {
        onDispose {
            tts?.shutdown()
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Load phrases data on screen load
    LaunchedEffect(Unit) {
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
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text(stringResource(R.string.common_phrases), color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }

            )
        }
    ) { innerPadding ->
        val selectedCategoryState = phrasesViewModel.selectedCategory.collectAsState()
        val selectedCategory = selectedCategoryState.value

        val filteredPhrasesState = phrasesViewModel.filteredPhrases.collectAsState()
        val filteredPhrases = filteredPhrasesState.value

        val categories = phrases
            .map { it.category }
            .distinct()
            .sorted()
            .toMutableList()
            .apply { add(0, "all") }

        @Composable
        fun getTranslatedCategory(category: String, phrases: List<Phrase>): String {
            val appLanguage = LocalAppLocale.current.currentLanguageCode
            return if (category == "all") {
                stringResource(R.string.all)
            } else {
                phrases.firstOrNull { it.category == category }
                    ?.categoryTranslations
                    ?.get(appLanguage)
                    ?: category
            }
        }

        val translatedCategories = categories.map { getTranslatedCategory(it, phrases) }

        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxWidth()
        ) {
            Dropdown(
                label = stringResource(R.string.select_category),
                options = translatedCategories,
                selectedOption = getTranslatedCategory(selectedCategory, phrases),
                onOptionSelected = { selectedLabel ->
                    val originalKey = categories[translatedCategories.indexOf(selectedLabel)]
                    phrasesViewModel.setSelectedCategory(originalKey)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                !errorMessage.isNullOrBlank() -> Text(
                    text = errorMessage ?: stringResource(R.string.unknown_error),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    items(filteredPhrases) { phrase ->
                        val phraseText = phrase.phrases[selectedLanguage] ?: phrase.phrases["hr"] ?: stringResource(R.string.unknown)
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