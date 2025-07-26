package com.example.crompass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.Phrase
import com.example.crompass.repository.PhraseRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class PhraseViewModel : ViewModel() {
    private val repository = PhraseRepository()

    // LiveData for phrases and loading/error state
    val phrases = MutableLiveData<List<Phrase>>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    // LiveData for user language (default is "en")
    val userLanguage = MutableLiveData<String>("en")

    // Fetch the user language from Firestore
    fun getUserLanguage(userId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val language = document.getString("language")
                    userLanguage.value = language ?: "en"  // Default to English if no language set
                }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = "Error fetching user language: ${exception.message}"
            }
    }

    // Fetch phrases
    fun getPhrases() {
        isLoading.value = true
        errorMessage.value = null
        viewModelScope.launch {
            try {
                val fetchedPhrases = repository.getPhrases()
                phrases.value = fetchedPhrases
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = "Error fetching phrases: ${e.message}"
                isLoading.value = false
            }
        }
    }
}