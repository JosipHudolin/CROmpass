package com.example.crompass.repository

import com.example.crompass.model.Phrase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun fetchPhrases(
    onComplete: (List<Phrase>) -> Unit,
    onError: (Exception) -> Unit
) {
    val db = Firebase.firestore
    db.collection("phrases")
        .get()
        .addOnSuccessListener { result ->
            val phrases = result.map { it.toObject(Phrase::class.java) }
            onComplete(phrases)
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}