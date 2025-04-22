package com.example.crompass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.crompass.ui.theme.CROmpassTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CROmpassTheme {
                var isAuthenticated by remember { mutableStateOf(Firebase.auth.currentUser != null) }

                if (isAuthenticated) {
                    HomeScreen(onLogout = { isAuthenticated = false })
                } else {
                    AuthScreen(onAuthSuccess = { isAuthenticated = true })
                }
            }
        }
    }
}

