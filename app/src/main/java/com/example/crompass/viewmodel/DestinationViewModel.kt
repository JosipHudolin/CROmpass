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
    val filteredLocations: StateFlow<List<Destination>> = _filteredLocations.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    init {
        fetchAllLocations()
    }

    private fun fetchAllLocations() {
        viewModelScope.launch {
            val locations = repository.fetchDestinations()
            _allLocations.value = locations
            applyFilter()
        }
    }

    fun updateCategory(category: String?) {
        _selectedCategory.value = category
        applyFilter()
    }

    private fun applyFilter() {
        val category = _selectedCategory.value
        _filteredLocations.value = if (category == null) {
            _allLocations.value
        } else {
            _allLocations.value.filter { it.category.equals(category, ignoreCase = true) }
        }
    }
}