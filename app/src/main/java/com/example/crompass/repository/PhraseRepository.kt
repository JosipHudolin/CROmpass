package com.example.crompass.repository

import com.example.crompass.model.Phrase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PhraseRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getPhrases(): List<Phrase> {
        return try {
            val snapshot = db.collection("phrases").get().await()
            snapshot.documents.map { doc ->
                val phrase = doc.toObject(Phrase::class.java)!!
                phrase.copy(categoryTranslations = doc.get("categoryTranslations") as? Map<String, String> ?: emptyMap())
            }
        } catch (e: Exception) {
            throw Exception("Error fetching phrases: ${e.message}")
        }
    }
}