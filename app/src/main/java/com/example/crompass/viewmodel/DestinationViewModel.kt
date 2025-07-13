package com.example.crompass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.Destination
import com.example.crompass.repository.DestinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DestinationViewModel : ViewModel() {
    private val repository = DestinationRepository()

    private val _destinations = MutableStateFlow<List<Destination>>(emptyList())
    val destinations: StateFlow<List<Destination>> = _destinations

    fun loadDestinations() {
        viewModelScope.launch {
            _destinations.value = repository.fetchDestinations()
        }
    }
}