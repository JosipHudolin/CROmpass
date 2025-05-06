package com.example.crompass.model

data class CultureRule(
    val category: String,
    val translations: Map<String, String> // Language code -> Translation
)
