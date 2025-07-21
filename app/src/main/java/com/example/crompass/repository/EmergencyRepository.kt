package com.example.crompass.repository

import com.example.crompass.model.EmergencyContact
import com.example.crompass.model.EmergencyTip
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EmergencyRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getEmergencyContacts(): List<EmergencyContact> {
        return try {
            db.collection("emergency_contacts")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(EmergencyContact::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getEmergencyTips(): List<EmergencyTip> {
        return try {
            db.collection("emergency_tips")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(EmergencyTip::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}