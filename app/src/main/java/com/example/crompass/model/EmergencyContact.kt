package com.example.crompass.model

data class EmergencyContact(
    val name: String = "",
    val number: String = "",
    val translatedNames: Map<String, String> = emptyMap()
)
