package com.example.crompass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.Destination
import com.example.crompass.repository.DestinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DestinationViewModel : ViewModel() {

    private val repository = DestinationRepository()

    private val _allLocations = MutableStateFlow<List<Destination>>(emptyList())
    private val _filteredLocations = MutableStateFlow<List<Destination>>(emptyList())
    val allLocations: StateFlow<List<Destination>> = _allLocations.asStateFlow()
    val filteredLocations: StateFlow<List<Destination>> = _filteredLocations.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _appLanguage = MutableStateFlow(java.util.Locale.getDefault().language)
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    init {
        getAllDestinations()
    }

    private fun getAllDestinations() {
        viewModelScope.launch {
            val locations = repository.getDestinations()
            _allLocations.value = locations
            applyFilter()
        }
    }

    fun updateCategory(category: String?) {
        _selectedCategory.value = category
        applyFilter()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    fun setAppLanguage(lang: String) {
        if (_appLanguage.value != lang) {
            _appLanguage.value = lang
            applyFilter()
        }
    }

    private fun applyFilter() {
        val category = _selectedCategory.value
        val query = _searchQuery.value.lowercase()
        val lang = _appLanguage.value

        _filteredLocations.value = _allLocations.value.filter { destination ->
            val matchesCategory = category == null || destination.category.equals(category, ignoreCase = true)

            val nameText = destination.nameTranslations[lang] ?: destination.name
            val descText = destination.descriptionTranslations[lang] ?: destination.description

            val matchesQuery = query.isBlank() ||
                nameText.lowercase().contains(query) ||
                descText.lowercase().contains(query)

            matchesCategory && matchesQuery
        }
    }

    fun getAllCategories(): List<String> {
        return _allLocations.value.map { it.category }.filter { it.isNotBlank() }.distinct()
    }
}