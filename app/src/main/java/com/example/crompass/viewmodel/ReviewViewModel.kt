package com.example.crompass.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crompass.model.Review
import com.example.crompass.repository.DestinationRepository
import com.example.crompass.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {

    private val repository = ReviewRepository()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _userReviews = MutableLiveData<List<Review>>()
    val userReviews: LiveData<List<Review>> = _userReviews

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val destinationRepository = DestinationRepository()

    private val _destinationNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val destinationNames: StateFlow<Map<String, String>> = _destinationNames

    fun getDestinationName(destinationId: String) {
        viewModelScope.launch {
            try {
                val name = destinationRepository.getDestinationNameById(destinationId)
                _destinationNames.value = _destinationNames.value + (destinationId to (name ?: "Unknown"))
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun getPublicReviewsByDestination(destinationId: String) {
        viewModelScope.launch {
            try {
                val result = repository.getReviewsForDestination(destinationId)
                _reviews.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun getAllPublicReviews() {
        viewModelScope.launch {
            try {
                val allReviews = repository.getAllPublicReviews()
                _reviews.value = allReviews
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun fetchUserReviews(userId: String) {
        viewModelScope.launch {
            try {
                val result = repository.getUserReviews(userId)
                _userReviews.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun submitReview(review: Review) {
        viewModelScope.launch {
            try {
                repository.addReview(review)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getPrivateUserReviews(userId: String) {
        viewModelScope.launch {
            try {
                val allUserReviews = repository.getUserReviews(userId)
                val privateReviews = allUserReviews.filter { !it.public }
                _userReviews.value = privateReviews
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}