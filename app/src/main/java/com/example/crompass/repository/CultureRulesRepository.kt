package com.example.crompass.repository

import com.example.crompass.model.CultureRule
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CultureRulesRepository {
    private val db = FirebaseFirestore.getInstance()

    // Fetch culture rules from Firestore
    suspend fun getCultureRules(): List<CultureRule> {
        val cultureRulesList = mutableListOf<CultureRule>()

        try {
            val snapshot = db.collection("culture_rules").get().await()
            for (document in snapshot.documents) {
                val category = document.getString("category") ?: ""
                val translations = document.get("translations") as? Map<String, String> ?: emptyMap()

                val cultureRule = CultureRule(category, translations)
                cultureRulesList.add(cultureRule)
            }
        } catch (e: Exception) {
            // Handle error (you might want to log or show the error message to the user)
            throw Exception("Error fetching culture rules: ${e.message}")
        }

        return cultureRulesList
    }

    // Add more methods if you need to insert, update, or delete rules
}