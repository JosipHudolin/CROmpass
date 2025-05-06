package com.example.crompass.repository

import com.example.crompass.model.Phrase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PhraseRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getPhrases(): List<Phrase> {
        return try {
            val snapshot = db.collection("phrases").get().await()
            snapshot.documents.map { it.toObject(Phrase::class.java)!! }
        } catch (e: Exception) {
            throw Exception("Error fetching phrases: ${e.message}")
        }
    }
}