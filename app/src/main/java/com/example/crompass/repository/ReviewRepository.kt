package com.example.crompass.repository

import com.example.crompass.model.Review
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reviewsCollection = db.collection("reviews")

    suspend fun addReview(review: Review) {
        reviewsCollection.add(review).await()
    }

    suspend fun getReviewsForDestination(destinationId: String): List<Review> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("destinationId", destinationId)
                .whereEqualTo("public", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUserReviews(userId: String): List<Review> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllPublicReviews(): List<Review> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("public", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}