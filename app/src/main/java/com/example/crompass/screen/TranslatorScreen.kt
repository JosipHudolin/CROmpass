package com.example.crompass.screen

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.R
import com.example.crompass.viewmodel.TranslatorViewModel
import com.example.crompass.model.TranslationResult
import com.example.crompass.screen.components.Dropdown
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

    val languageLabelToCode = mapOf(
        stringResource(R.string.english) to "en",
        stringResource(R.string.german) to "de",
        stringResource(R.string.french) to "fr",
        stringResource(R.string.italian) to "it",
        stringResource(R.string.croatian) to "hr",
        stringResource(R.string.polish) to "pl"
    )

    val languageOptions = languageLabelToCode.keys.toList()

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
            TopAppBar(
                windowInsets = WindowInsets(0), // ⬅️ ovo makne status bar padding
                title = {
                    Text(
                        stringResource(R.string.translate),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = {
                    Text(
                        stringResource(R.string.translate),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            Dropdown(
                label = stringResource(R.string.target_langugage),
                options = languageOptions,
                selectedOption = languageLabelToCode.entries.firstOrNull { it.value == selectedLanguage }?.key
                    ?: stringResource(R.string.english),
                onOptionSelected = { selectedName ->
                    selectedLanguage = languageLabelToCode[selectedName] ?: "en"
                }
            )

            Button(
                onClick = { viewModel.translateText(inputText, selectedLanguage) },
                enabled = inputText.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    stringResource(R.string.translation),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            translationResult?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Translated: ${it.translatedText}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                tts?.language = Locale(selectedLanguage)
                                tts?.speak(it.translatedText, TextToSpeech.QUEUE_FLUSH, null, null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                stringResource(R.string.speak),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            if (recentTranslations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    stringResource(R.string.recent_translation),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyColumn {
                    items(recentTranslations) { item ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = item.originalText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "→ ${item.translatedText} (${item.targetLanguageCode})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
