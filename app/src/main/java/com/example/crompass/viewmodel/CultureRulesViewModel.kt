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

    private val _selectedLanguage = MutableLiveData("en")
    val selectedLanguage: LiveData<String> = _selectedLanguage

    private val _selectedCategory = MutableLiveData("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to hold the list of culture rules
    var cultureRules = mutableStateListOf<CultureRule>()

    init {
        getCultureRules()
        getUserLanguage()
    }

    // Fetch culture rules from the repository
    private fun getCultureRules() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val rules = repository.getCultureRules()
                if (rules.isEmpty()) {
                    Log.d("CultureRulesViewModel", "No culture rules found")
                } else {
                    Log.d("CultureRulesViewModel", "Culture rules fetched: ${rules.size}")
                }

                cultureRules.clear()
                cultureRules.addAll(rules)
            } catch (e: Exception) {
                Log.e("CultureRulesViewModel", "Error fetching culture rules: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserLanguage() {
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

    fun setSelectedLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun getFilteredCultureRules(): List<CultureRule> {
        val language = _selectedLanguage.value ?: "en"
        val category = _selectedCategory.value ?: "All"

        return cultureRules.filter { rule ->
            (category == "All" || rule.category.replace("_", " ").replaceFirstChar { it.uppercase() } == category) &&
            rule.translations.containsKey(language)
        }
    }
}