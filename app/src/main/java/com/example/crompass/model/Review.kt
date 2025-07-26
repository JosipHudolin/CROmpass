package com.example.crompass.model

import com.google.firebase.Timestamp

data class Review(
    val userId: String = "",
    val destinationId: String = "",
    val reviewText: String = "",
    val rating: Int = 0,
    val public: Boolean = false, // Zbog prevođenja u Javu
    val timestamp: Timestamp = Timestamp.now()
)
