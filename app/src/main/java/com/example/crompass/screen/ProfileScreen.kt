package com.example.crompass.screen

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavHostController
import com.example.crompass.utils.logout
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val db = Firebase.firestore
    val userId = Firebase.auth.currentUser?.uid

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userData = document.data
                    } else {
                        error = "No user data found."
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    error = "Error fetching profile: ${it.message}"
                    isLoading = false
                }
        } else {
            error = "User not logged in."
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            CROmpassBottomBar(navController = navController, currentRoute = "profile")
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Text(text = error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
                userData != null -> {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        listOf(
                            "👤 Name" to "${userData?.get("firstName")} ${userData?.get("lastName")}",
                            "📧 Email" to "${userData?.get("email")}",
                            "🎂 Age" to "${userData?.get("age")}",
                            "⚧ Gender" to "${userData?.get("gender")}",
                            "🌍 Country" to "${userData?.get("country")}",
                            "🗣️ Language" to "${userData?.get("language")}"
                        ).forEach { (label, value) ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = label, style = MaterialTheme.typography.labelMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = value, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                showEditDialog = true
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Edit Info")
                        }

                        Button(
                            onClick = {
                                showPasswordDialog = true
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Change Password")
                        }

                        Button(
                            onClick = {
                                logout(navController)
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
    if (showEditDialog && userData != null) {
        EditProfileDialog(
            currentData = userData!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedData ->
                val userId = Firebase.auth.currentUser?.uid ?: return@EditProfileDialog
                db.collection("users").document(userId).set(updatedData)
                    .addOnSuccessListener {
                        userData = updatedData
                        showEditDialog = false
                    }
            }
        )
    }
    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onPasswordChanged = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
                showPasswordDialog = false
            }
        )
    }
}

@Composable
fun EditProfileDialog(
    currentData: Map<String, Any>,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any>) -> Unit
) {
    var firstName by remember { mutableStateOf(currentData["firstName"] as? String ?: "") }
    var lastName by remember { mutableStateOf(currentData["lastName"] as? String ?: "") }
    var age by remember { mutableStateOf(currentData["age"] as? String ?: "") }
    var gender by remember { mutableStateOf(currentData["gender"] as? String ?: "") }
    var country by remember { mutableStateOf(currentData["country"] as? String ?: "") }
    var language by remember { mutableStateOf(currentData["language"] as? String ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "age" to age,
                        "gender" to gender,
                        "country" to country,
                        "language" to language,
                        "email" to currentData["email"].toString()
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") })
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") })
                OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") })

                SimpleDropdown(
                    label = "Gender",
                    options = listOf("Male", "Female", "Other"),
                    selectedOption = gender,
                    onOptionSelected = { gender = it }
                )

                SimpleDropdown(
                    label = "Country",
                    options = listOf(
                        "Croatia",
                        "Germany",
                        "France",
                        "Italy",
                        "Spain",
                        "United Kingdom",
                        "Poland",
                        "Netherlands",
                        "Belgium",
                        "Switzerland"
                    ),
                    selectedOption = country,
                    onOptionSelected = { country = it }
                )

                SimpleDropdown(
                    label = "Language",
                    options = listOf(
                        "Croatian",
                        "English",
                        "German",
                        "French",
                        "Italian",
                        "Spanish",
                        "Polish",
                        "Dutch"
                    ),
                    selectedOption = language,
                    onOptionSelected = { language = it }
                )
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onPasswordChanged: (String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val user = Firebase.auth.currentUser
                val email = user?.email

                if (newPassword != confirmPassword) {
                    errorMessage = "Passwords do not match."
                    return@TextButton
                }

                if (email != null && user != null) {
                    val credential = EmailAuthProvider.getCredential(email, currentPassword)
                    user.reauthenticate(credential)
                        .addOnSuccessListener {
                            user.updatePassword(newPassword)
                                .addOnSuccessListener {
                                    onPasswordChanged("Password changed successfully.")
                                }
                                .addOnFailureListener {
                                    onPasswordChanged("Failed to change password: ${it.message}")
                                }
                        }
                        .addOnFailureListener {
                            onPasswordChanged("Authentication failed: ${it.message}")
                        }
                } else {
                    onPasswordChanged("No authenticated user.")
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Change Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}
