package com.example.crompass.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.crompass.model.UserData
import com.example.crompass.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val userRepository = UserRepository(FirebaseFirestore.getInstance())

    // Use coroutines to fetch user data
    fun fetchUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                try {
                    // Fetch the data asynchronously
                    val data = userRepository.getUserData(userId)
                    if (data != null) {
                        _userData.postValue(data) // Post the result to LiveData
                    } else {
                        _errorMessage.postValue("No user data found.")
                    }
                } catch (e: Exception) {
                    _errorMessage.postValue("Error fetching data: ${e.message}")
                }
            }
        } else {
            _errorMessage.postValue("User not logged in.")
        }
    }

    fun updateUserData(updatedData: Map<String, Any>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                try {
                    userRepository.updateUserData(userId, updatedData)
                    fetchUserData() // Refetch updated data
                } catch (e: Exception) {
                    _errorMessage.postValue("Error updating data: ${e.message}")
                }
            }
        } else {
            _errorMessage.postValue("User not logged in.")
        }
    }
}