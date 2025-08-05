package com.example.crompass.utils

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.navigation.NavHostController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore

fun registerUser(email: String, password: String, userData: Map<String, Any>, onResult: (Boolean, String?) -> Unit) {
    val auth = Firebase.auth
    val db = Firebase.firestore

    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { authResult ->
            val userId = authResult.user?.uid
            if (userId != null) {
                db.collection("users").document(userId)
                    .set(userData)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            } else {
                onResult(false, "Failed to get user ID.")
            }
        }
        .addOnFailureListener { e ->
            onResult(false, e.message)
        }
}


fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
    Firebase.auth.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener {
            onResult(true, null)
        }
        .addOnFailureListener { e ->
            onResult(false, e.message)
        }
}

fun logout(navController: NavHostController) {
    Firebase.auth.signOut()
    navController.navigate("auth") {
        popUpTo(0) { inclusive = true } // Clears the whole backstack
    }
}
fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, "Reset email sent")
            } else {
                onResult(false, task.exception?.localizedMessage ?: "Unknown error")
            }
        }
}


fun changePassword(
    currentPassword: String,
    newPassword: String,
    onResult: (Boolean, String?) -> Unit
) {
    val user = Firebase.auth.currentUser
    val email = user?.email

    if (user != null && email != null) {
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        onResult(true, null)
                    }
                    .addOnFailureListener { e ->
                        onResult(false, e.message)
                    }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    } else {
        onResult(false, "No authenticated user")
    }
}