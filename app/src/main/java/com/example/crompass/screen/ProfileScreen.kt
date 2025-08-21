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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.model.UserData
import com.example.crompass.viewmodel.UserViewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.example.crompass.R
import com.example.crompass.screen.components.Dropdown
import com.example.crompass.utils.LocalAppLocale
import com.example.crompass.utils.changePassword
import com.example.crompass.utils.logout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, globalNavController: NavHostController, viewModel: UserViewModel = viewModel()) {
    val userData by viewModel.userData.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()
    var isEditDialogOpen by remember { mutableStateOf(false) }
    var isChangePasswordDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.getUserData()
    }

    // Get current locale and translation maps
    val appLocaleLanguage = LocalAppLocale.current.currentLanguageCode

    // --- content descriptions (avoid calling stringResource inside semantics) ---
    val cdMyProfile = stringResource(R.string.my_profile)
    val cdBack = stringResource(R.string.back)
    val cdEditInfo = stringResource(R.string.edit_info)
    val cdChangePassword = stringResource(R.string.change_password)
    val cdLogout = stringResource(R.string.logout)

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = { Text(text = cdMyProfile, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = cdBack, tint = MaterialTheme.colorScheme.onPrimary)
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
                            stringResource(R.string.gender) to (
                                when (userData?.gender?.lowercase()) {
                                    "male" -> stringResource(R.string.male)
                                    "female" -> stringResource(R.string.female)
                                    else -> stringResource(R.string.unknown)
                                }
                            )
                        ).forEach { (label, value) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "$label: $value"
                                    },
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
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .semantics { contentDescription = cdEditInfo }
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
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .semantics { contentDescription = cdChangePassword }
                        ) {
                            Text(stringResource(R.string.change_password))
                        }

                        if (isChangePasswordDialogOpen) {
                            ChangePasswordDialog(
                                onDismiss = { isChangePasswordDialogOpen = false },
                                onPasswordChanged = { message ->
                                    println(message)
                                }
                            )
                        }
                        Button(
                            onClick = {
                                logout(globalNavController)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .semantics { contentDescription = cdLogout }
                        ) {
                            Text(stringResource(id = R.string.logout))
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

    val context = LocalContext.current
    // Localized gender options
    val genderOptions = listOf(
        stringResource(R.string.male),
        stringResource(R.string.female)
    )
    // Map to convert localized gender back to English key
    val genderToEnglish = mapOf(
        context.getString(R.string.male) to "male",
        context.getString(R.string.female) to "female"
    )
    // When showing the dropdown, display the localized value for the current gender
    val selectedGenderLocalized = genderToEnglish.entries.find { it.value == gender }?.key ?: genderOptions.first()

    // --- content descriptions (precompute for semantics) ---
    val cdName = stringResource(R.string.name)
    val cdLastName = stringResource(R.string.last_name)
    val cdEmail = stringResource(R.string.email)
    val cdAge = stringResource(R.string.age)
    val cdGender = stringResource(R.string.gender)
    val cdSave = stringResource(R.string.save)
    val cdCancel = stringResource(R.string.cancel)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_info)) },
        text = {
            Column {
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.semantics { contentDescription = cdName },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(stringResource(R.string.last_name)) },
                    modifier = Modifier.semantics { contentDescription = cdLastName },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.email)) },
                    modifier = Modifier.semantics { contentDescription = cdEmail },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text(stringResource(R.string.age)) },
                    modifier = Modifier.semantics { contentDescription = cdAge },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                // Gender Dropdown
                Box(
                    modifier = Modifier.semantics { contentDescription = cdGender }
                ) {
                    Dropdown(
                        label = stringResource(R.string.gender),
                        options = genderOptions,
                        selectedOption = genderOptions.find { it == selectedGenderLocalized } ?: genderOptions.first(),
                        onOptionSelected = { selected ->
                            gender = genderToEnglish[selected] ?: genderToEnglish.values.firstOrNull { it == selected } ?: "Other"
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "age" to age,
                            "gender" to (genderToEnglish[gender] ?: gender ?: "Other")
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier.semantics { contentDescription = cdSave }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.semantics { contentDescription = cdCancel }
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
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

    val context = LocalContext.current

    // --- content descriptions (precompute for semantics) ---
    val cdCurrentPassword = stringResource(R.string.current_password)
    val cdNewPassword = stringResource(R.string.new_password)
    val cdConfirmPassword = stringResource(R.string.confirm_password)
    val cdSave = stringResource(R.string.save)
    val cdCancel = stringResource(R.string.cancel)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.change_password)) },
        text = {
            Column {
                TextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text(stringResource(R.string.current_password)) },
                    modifier = Modifier.semantics { contentDescription = cdCurrentPassword },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                TextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.new_password)) },
                    modifier = Modifier.semantics { contentDescription = cdNewPassword },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.confirm_password)) },
                    modifier = Modifier.semantics { contentDescription = cdConfirmPassword },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newPassword == confirmPassword) {
                        changePassword(currentPassword, newPassword) { success, message ->
                            if (success) {
                                Toast.makeText(context, context.getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, context.getString(R.string.error_changing_password), Toast.LENGTH_SHORT).show()
                            }
                            onPasswordChanged(message ?: "")
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
                        onPasswordChanged(context.getString(R.string.passwords_do_not_match))
                    }
                    onDismiss()
                },
                modifier = Modifier.semantics { contentDescription = cdSave }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.semantics { contentDescription = cdCancel }
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}