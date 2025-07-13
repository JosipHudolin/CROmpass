package com.example.crompass.repository

import com.example.crompass.model.Destination
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DestinationRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchDestinations(): List<Destination> {
        return try {
            val snapshot = db.collection("destinations").get().await()
            snapshot.documents.mapNotNull { it.toObject(Destination::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}