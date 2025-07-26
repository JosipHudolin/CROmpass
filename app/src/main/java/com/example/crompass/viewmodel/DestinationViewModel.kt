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

    private fun applyFilter() {
        val category = _selectedCategory.value
        val query = _searchQuery.value.lowercase()
        _filteredLocations.value = _allLocations.value.filter { destination ->
            val matchesCategory = category == null || destination.category.equals(category, ignoreCase = true)
            val matchesQuery = destination.name.lowercase().contains(query) || destination.description.lowercase().contains(query)
            matchesCategory && matchesQuery
        }
    }

    fun getAllCategories(): List<String> {
        return _allLocations.value.mapNotNull { it.category }.distinct()
    }
}