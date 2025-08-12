package com.example.crompass.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.TranslationResult
import com.example.crompass.repository.TranslatorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TranslatorViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = TranslatorRepository(app.applicationContext)

    private val _translationResult = MutableLiveData<TranslationResult?>()
    val translationResult: LiveData<TranslationResult?> = _translationResult

    val recentTranslations: LiveData<List<TranslationResult>> =
        repository.getRecentTranslations().asLiveData()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun translateText(text: String, targetLanguage: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = withContext(Dispatchers.IO) {
                repository.translate(text, targetLanguage)
            }

            _translationResult.value = result

            repository.addRecent(result)

            _isLoading.value = false
        }
    }

}