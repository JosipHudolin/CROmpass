package com.example.crompass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.EmergencyContact
import com.example.crompass.model.EmergencyTip
import com.example.crompass.repository.EmergencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmergencyViewModel : ViewModel() {
    private val repository = EmergencyRepository()

    private val _contacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val contacts: StateFlow<List<EmergencyContact>> = _contacts

    private val _tips = MutableStateFlow<List<EmergencyTip>>(emptyList())
    val tips: StateFlow<List<EmergencyTip>> = _tips

    fun fetchData() {
        viewModelScope.launch {
            _contacts.value = repository.getEmergencyContacts()
            _tips.value = repository.getEmergencyTips()
        }
    }
}