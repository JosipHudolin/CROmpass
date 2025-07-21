package com.example.crompass.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.crompass.model.UserData
import kotlinx.coroutines.tasks.await

class UserRepository(private val db: FirebaseFirestore) {

    suspend fun getUserData(userId: String): UserData? {
        return try {
            val document = db.collection("users").document(userId).get().await()
            if (document.exists()) {
                val data = document.data
                UserData(
                    firstName = data?.get("firstName") as String,
                    lastName = data["lastName"] as String,
                    email = data["email"] as String,
                    age = data["age"] as String,
                    gender = data["gender"] as String,
                    country = data["country"] as String,
                    language = data["language"] as String
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserLanguage(userId: String): String? {
        return try {
            val document = db.collection("users").document(userId).get().await()
            if (document.exists()) {
                document.getString("language")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserData(userId: String, updatedData: Map<String, Any>) {
        try {
            db.collection("users").document(userId).update(updatedData).await()
        } catch (e: Exception) {
            throw e
        }
    }
}