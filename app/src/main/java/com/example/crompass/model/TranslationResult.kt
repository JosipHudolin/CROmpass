package com.example.crompass.model

data class TranslationResult(
    val originalText: String,
    val translatedText: String,
    val sourceLanguageCode: String,
    val targetLanguageCode: String,
    val timestamp: Long = System.currentTimeMillis()
)