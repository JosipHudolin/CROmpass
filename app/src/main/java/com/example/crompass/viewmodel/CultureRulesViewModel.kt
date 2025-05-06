package com.example.crompass.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.CultureRule
import com.example.crompass.repository.CultureRulesRepository
import kotlinx.coroutines.launch

class CultureRulesViewModel : ViewModel() {
    private val repository = CultureRulesRepository()

    // LiveData to hold the list of culture rules
    var cultureRules = mutableStateListOf<CultureRule>()

    init {
        fetchCultureRules()
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
}