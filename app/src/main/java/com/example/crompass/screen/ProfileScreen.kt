package com.example.crompass.screen

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.model.UserData
import com.example.crompass.utils.logout
import com.example.crompass.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.stringResource
import com.example.crompass.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, viewModel: UserViewModel = viewModel()) {
    val userData by viewModel.userData.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()
    var isEditDialogOpen by remember { mutableStateOf(false) }
    var isChangePasswordDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.getUserData()
    }

    // Reverse mapping: language code to language name
    val languageCodeToName = mapOf(
        "en" to "English",
        "de" to "German",
        "fr" to "French",
        "it" to "Italian",
        "es" to "Spanish",
        "nl" to "Dutch",
        "hr" to "Croatian",
        "pl" to "Polish",
        "sv" to "Swedish",
        "da" to "Danish",
        "no" to "Norwegian",
        "fi" to "Finnish",
        "sk" to "Slovak",
        "sl" to "Slovenian",
        "hu" to "Hungarian",
        "cs" to "Czech"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = { Text(text = stringResource(R.string.my_profile), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            when {
                userData == null -> {
                    if (errorMessage != null) {
                        Text(text = errorMessage ?: stringResource(R.string.unknown), color = MaterialTheme.colorScheme.error)
                    } else {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        listOf(
                            stringResource(R.string.name) to "${userData?.firstName} ${userData?.lastName}",
                            stringResource(R.string.email) to "${userData?.email}",
                            stringResource(R.string.age) to "${userData?.age}",
                            stringResource(R.string.gender) to "${userData?.gender}",
                            stringResource(R.string.country) to "${userData?.country}",
                            stringResource(R.string.language) to (languageCodeToName[userData?.language] ?: stringResource(R.string.unknown))
                        ).forEach { (label, value) ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
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
                            onClick = { isEditDialogOpen = true },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(stringResource(R.string.edit_info))
                        }

                        if (isEditDialogOpen) {
                            EditProfileDialog(
                                userData = userData!!,
                                onDismiss = { isEditDialogOpen = false },
                                onSave = { updatedData ->
                                    viewModel.updateUserData(updatedData) // Call ViewModel method to update Firestore
                                    isEditDialogOpen = false
                                }
                            )
                        }

                        Button(
                            onClick = { isChangePasswordDialogOpen = true },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(stringResource(R.string.change_password))
                        }

                        if (isChangePasswordDialogOpen) {
                            ChangePasswordDialog(
                                onDismiss = { isChangePasswordDialogOpen = false },
                                onPasswordChanged = { message ->
                                    // Handle password changed message
                                    // Show a Toast or update UI accordingly
                                    println(message)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    userData: UserData,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any>) -> Unit
) {
    var firstName by remember { mutableStateOf(userData.firstName) }
    var lastName by remember { mutableStateOf(userData.lastName) }
    var email by remember { mutableStateOf(userData.email) }
    var age by remember { mutableStateOf(userData.age) }
    var gender by remember { mutableStateOf(userData.gender) }
    var country by remember { mutableStateOf(userData.country) }
    var language by remember { mutableStateOf(userData.language) }

    // Dropdown options
    val genderOptions = listOf("Male", "Female", "Other")
    val countryOptions = listOf(
        "Austria", "Belgium", "Croatia", "Czech Republic", "Denmark", "Estonia", "Finland", "France", "Germany", "Greece", "Hungary", "Ireland", "Italy", "Latvia", "Lithuania", "Netherlands", "Norway", "Poland", "Portugal", "Slovakia", "Slovenia", "Spain", "Sweden", "Switzerland"
    )

    val languageOptions = listOf(
        "English", "German", "French", "Italian", "Croatian", "Polish"
    )

    // Language codes map
    val languageCodes = mapOf(
        "English" to "en",
        "German" to "de",
        "French" to "fr",
        "Italian" to "it",
        "Croatian" to "hr",
        "Polish" to "pl"
    )

    val languageCodeToName = mapOf(
        "en" to "English",
        "de" to "German",
        "fr" to "French",
        "it" to "Italian",
        "hr" to "Croatian",
        "pl" to "Polish"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_info)) },
        text = {
            Column {
                TextField(value = firstName, onValueChange = { firstName = it }, label = { Text(stringResource(R.string.name)) })
                TextField(value = lastName, onValueChange = { lastName = it }, label = { Text(stringResource(R.string.last_name)) })
                TextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.email)) })
                TextField(value = age, onValueChange = { age = it }, label = { Text(stringResource(R.string.age)) })

                // Gender Dropdown
                SimpleDropdown(
                    label = stringResource(R.string.gender),
                    options = genderOptions,
                    selectedOption = gender,
                    onOptionSelected = { gender = it }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Country Dropdown
                SimpleDropdown(
                    label = stringResource(R.string.country),
                    options = countryOptions,
                    selectedOption = country,
                    onOptionSelected = { country = it }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Language Dropdown (show names but save the language code)
                SimpleDropdown(
                    label = stringResource(R.string.language),
                    options = languageOptions,
                    selectedOption = languageCodeToName[language] ?: "English",
                    onOptionSelected = { selectedLanguage ->
                        language = languageCodes[selectedLanguage] ?: "en"
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedLanguageCode = languageCodes.entries.find { it.value == language }?.value ?: language
                onSave(
                    mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email,
                        "age" to age,
                        "gender" to gender,
                        "country" to country,
                        "language" to selectedLanguageCode // Save the language code here
                    )
                )
                onDismiss()
            }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
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

    // Get the current context using LocalContext
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.change_password)) },
        text = {
            Column {
                TextField(value = currentPassword, onValueChange = { currentPassword = it }, label = { Text(stringResource(R.string.current_password)) })
                TextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text(stringResource(R.string.new_password)) })
                TextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text(stringResource(R.string.confirm_password)) })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                FirebaseAuth.getInstance().currentUser?.let { user ->
                    if (newPassword == confirmPassword) {
                        if (newPassword.length >= 6) { // Password length validation
                            user.updatePassword(newPassword)
                                .addOnSuccessListener {
                                    // Show Toast that password was successfully changed
                                    Toast.makeText(context, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                                    onPasswordChanged("Password changed successfully.")
                                }
                                .addOnFailureListener { e ->
                                    onPasswordChanged("Error changing password: ${e.localizedMessage}")
                                }
                        } else {
                            onPasswordChanged("Password must be at least 6 characters.")
                        }
                    } else {
                        onPasswordChanged("Passwords do not match.")
                    }
                } ?: run {
                    onPasswordChanged("User is not logged in.")
                }
                onDismiss()
            }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}