package com.example.crompass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.Phrase
import com.example.crompass.repository.PhraseRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PhraseViewModel : ViewModel() {
    private val repository = PhraseRepository()

    // LiveData for phrases and loading/error state
    val phrases = MutableLiveData<List<Phrase>>()

    private val _filteredPhrases = MutableStateFlow<List<Phrase>>(emptyList())
    val filteredPhrases: StateFlow<List<Phrase>> = _filteredPhrases

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory
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
                filterPhrases() // filtriraj na temelju trenutne kategorije
                isLoading.value = false
            } catch (e: Exception) {
                errorMessage.value = "Error fetching phrases: ${e.message}"
                isLoading.value = false
            }
        }
    }

    // Postavi odabranu kategoriju i filtriraj fraze
    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
        filterPhrases()
    }

    // Filtriraj fraze prema odabranoj kategoriji
    private fun filterPhrases() {
        val categoryKey = _selectedCategory.value
        val allPhrases = phrases.value ?: emptyList()

        _filteredPhrases.value = if (categoryKey == "all") {
            allPhrases
        } else {
            allPhrases.filter { phrase ->
                phrase.category == categoryKey || phrase.categoryTranslations?.get(userLanguage.value ?: "en") == categoryKey
            }
        }
    }
}