package com.example.crompass.repository

import com.example.crompass.model.Destination
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DestinationRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getDestinations(category: String? = null): List<Destination> {
        return try {
            val query = if (category.isNullOrBlank()) {
                db.collection("destinations")
            } else {
                db.collection("destinations").whereEqualTo("category", category)
            }

            val snapshot = query.get().await()
            snapshot.documents.mapNotNull { it.toObject(Destination::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDestinationNameById(destinationId: String): String? {
        return try {
            val document = db.collection("destinations").document(destinationId).get().await()
            document.getString("name")
        } catch (e: Exception) {
            null
        }
    }
}