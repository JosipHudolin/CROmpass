package com.example.crompass.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.CultureRule
import com.example.crompass.repository.CultureRulesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class CultureRulesViewModel : ViewModel() {
    private val repository = CultureRulesRepository()

    private val _userLanguage = MutableLiveData("en")
    val userLanguage: LiveData<String> = _userLanguage

    // LiveData to hold the list of culture rules
    var cultureRules = mutableStateListOf<CultureRule>()

    init {
        fetchCultureRules()
        fetchUserLanguage()
    }

    // Fetch culture rules from the repository
    private fun fetchCultureRules() {
        viewModelScope.launch {
            try {
                // Get culture rules from the repository
                val rules = repository.getCultureRules()
                if (rules.isEmpty()) {
                    Log.d("CultureRulesViewModel", "No culture rules found")
                } else {
                    Log.d("CultureRulesViewModel", "Culture rules fetched: ${rules.size}")
                }

                cultureRules.clear()
                cultureRules.addAll(rules)
            } catch (e: Exception) {
                // Handle error (you might want to display an error message to the user)
                Log.e("CultureRulesViewModel", "Error fetching culture rules: ${e.message}")
            }
        }
    }

    fun fetchUserLanguage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val lang = document.getString("language") ?: "en"
                _userLanguage.value = lang
            }
            .addOnFailureListener {
                _userLanguage.value = "en"
            }
    }
}