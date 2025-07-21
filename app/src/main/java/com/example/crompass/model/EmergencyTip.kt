package com.example.crompass.model

data class EmergencyTip(
    val category: String = "",
    val translations: Map<String, String> = emptyMap()
)
