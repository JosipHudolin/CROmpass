package com.example.crompass.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.TranslationResult
import com.example.crompass.repository.TranslatorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TranslatorViewModel : ViewModel() {

    private val repository = TranslatorRepository()

    private val _translationResult = MutableLiveData<TranslationResult?>()
    val translationResult: LiveData<TranslationResult?> = _translationResult

    private val _recentTranslations = MutableLiveData<List<TranslationResult>>(emptyList())
    val recentTranslations: LiveData<List<TranslationResult>> = _recentTranslations

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun translateText(text: String, targetLanguage: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = withContext(Dispatchers.IO) {
                repository.translate(text, targetLanguage)
            }

            _translationResult.value = result

            val updatedList = listOf(result) + (_recentTranslations.value ?: emptyList())
            _recentTranslations.value = updatedList.take(5)

            _isLoading.value = false
        }
    }
}