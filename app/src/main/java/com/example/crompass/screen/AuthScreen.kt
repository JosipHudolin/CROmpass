package com.example.crompass.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.crompass.R
import com.example.crompass.utils.loginUser
import com.example.crompass.utils.registerUser

@Composable
fun AuthScreen(
    navController: NavHostController
) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val fillAllFields = stringResource(R.string.fill_all_fields)
    val ageNumberRequired = stringResource(R.string.age_number_required)
    val loginLabel = stringResource(R.string.login)
    val registerLabel = stringResource(R.string.register)
    val noAccountRegister = stringResource(R.string.no_account_register)
    val haveAccountLogin = stringResource(R.string.have_account_login)

    // Dropdown options and expanded states
    val genderOptions = listOf("Male", "Female", "Other")

    val countryOptions = listOf(
        "Austria", "Belgium", "Croatia", "Czech Republic", "Denmark", "Estonia", "Finland", "France", "Germany", "Greece", "Hungary", "Ireland", "Italy", "Latvia", "Lithuania", "Netherlands", "Norway", "Poland", "Portugal", "Slovakia", "Slovenia", "Spain", "Sweden", "Switzerland"
    )

    val languageOptions = listOf(
        "English", "German", "French", "Italian", "Croatian", "Polish"
    )

    val scrollState = rememberScrollState()
    val passwordsNoMatch = stringResource(R.string.passwords_no_match)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = if (isLoginMode) loginLabel else registerLabel,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isLoginMode) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(R.string.name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(stringResource(R.string.last_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text(stringResource(R.string.age)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                SimpleDropdown(
                    label = stringResource(R.string.gender),
                    options = genderOptions,
                    selectedOption = gender,
                    onOptionSelected = { gender = it }
                )
                Spacer(modifier = Modifier.height(8.dp))

                SimpleDropdown(
                    label = stringResource(R.string.country),
                    options = countryOptions,
                    selectedOption = country,
                    onOptionSelected = { country = it }
                )
                Spacer(modifier = Modifier.height(8.dp))

                SimpleDropdown(
                    label = stringResource(R.string.language),
                    options = languageOptions,
                    selectedOption = language,
                    onOptionSelected = { language = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = if (isLoginMode) ImeAction.Done else ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!isLoginMode) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.confirm_password)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    if (isLoginMode) {
                        loginUser(email, password) { success, message ->
                            isLoading = false
                            if (success) {
                                navController.navigate("main") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            } else {
                                errorMessage = message
                            }
                        }
                    } else {
                        if (firstName.isBlank() || lastName.isBlank() || age.isBlank() ||
                            gender.isBlank() || country.isBlank() || language.isBlank()
                        ) {
                            errorMessage = fillAllFields
                            isLoading = false
                            return@Button
                        }

                        if (age.toIntOrNull() == null) {
                            errorMessage = ageNumberRequired
                            isLoading = false
                            return@Button
                        }

                        if (password != confirmPassword) {
                            errorMessage = passwordsNoMatch
                            isLoading = false
                            return@Button
                        }

                        val languageCodes = mapOf(
                            "English" to "en",
                            "German" to "de",
                            "French" to "fr",
                            "Italian" to "it",
                            "Croatian" to "hr",
                            "Polish" to "pl"
                        )

                        val languageCode = languageCodes[language] ?: "en"

                        val userData = mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "age" to age,
                            "gender" to gender,
                            "country" to country,
                            "language" to languageCode,
                            "email" to email
                        )
                        registerUser(email, password, userData) { success, message ->
                            isLoading = false
                            if (success) {
                                navController.navigate("main") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            } else {
                                errorMessage = message
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = email.isNotBlank() && password.length >= 6,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isLoginMode) loginLabel else registerLabel)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { isLoginMode = !isLoginMode },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    if (isLoginMode) noAccountRegister
                    else haveAccountLogin
                )
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
            }

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun SimpleDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clickable { expanded = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedOption.ifBlank { stringResource(R.string.select_option) },
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = { expanded = !expanded },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown"
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onOptionSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
