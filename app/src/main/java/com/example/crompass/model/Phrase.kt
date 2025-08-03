package com.example.crompass.model

data class Phrase(
    val category: String = "",
    val categoryTranslations: Map<String, String> = emptyMap(),
    val phrases: Map<String, String> = emptyMap()
)