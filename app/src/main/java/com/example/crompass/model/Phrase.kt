package com.example.crompass.model

data class Phrase(
    val category: String = "",
    val phrases: Map<String, String> = emptyMap()
)