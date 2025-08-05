package com.example.crompass.screen

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.crompass.R
import com.example.crompass.screen.components.Dropdown
import com.example.crompass.utils.LocalAppLocale
import com.example.crompass.utils.loginUser
import com.example.crompass.utils.registerUser
import com.example.crompass.utils.sendPasswordReset

@Composable
fun AuthScreen(
    navController: NavHostController
) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
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

    val appLocale = LocalAppLocale.current
    var selectedLanguage by remember { mutableStateOf(appLocale.currentLanguageCode) }
    var languageDropdownExpanded by remember { mutableStateOf(false) }

    val languages = mapOf(
        "English 🇬🇧" to "en",
        "Français 🇫🇷" to "fr",
        "Deutsch 🇩🇪" to "de",
        "Italiano 🇮🇹" to "it",
        "Polski 🇵🇱" to "pl",
        "Hrvatski 🇭🇷" to "hr"
    )

    val genderTranslationMap = mapOf(
        stringResource(R.string.male) to "male",
        stringResource(R.string.female) to "female"
    )
    val genderOptions = genderTranslationMap.keys.toList()


    val scrollState = rememberScrollState()
    val passwordsNoMatch = stringResource(R.string.passwords_no_match)

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_crompass),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(20.dp, 0.dp)
                        .align(Alignment.Center)
                )
            }

            Text(
                modifier = Modifier.padding(top = 20.dp, bottom = 0.dp),
                text = if (isLoginMode) loginLabel else registerLabel,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.secondary
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

                Dropdown(
                    label = stringResource(R.string.gender),
                    options = genderOptions,
                    selectedOption = genderTranslationMap.entries.firstOrNull { it.value == gender }?.key ?: "",
                    onOptionSelected = { selectedTranslated ->
                        gender = genderTranslationMap[selectedTranslated] ?: ""
                    }
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
                                password = ""
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.invalid_email_or_password),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        if (firstName.isBlank() || lastName.isBlank() || age.isBlank() ||
                            gender.isBlank()
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

                        val userData = mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "age" to age,
                            "gender" to gender,
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

            if (isLoginMode) {
                TextButton(
                    onClick = {
                        if (email.isNotBlank()) {
                            sendPasswordReset(email) { success, message ->
                                Toast.makeText(
                                    context,
                                    if (success) context.getString(R.string.password_reset_success) else message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.enter_email_for_reset),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.forgot_password))
                }
                Dropdown(
                    label = stringResource(R.string.change_language),
                    options = languages.keys.toList(),
                    selectedOption = languages.entries.firstOrNull { it.value == selectedLanguage }?.key ?: "",
                    onOptionSelected = { label ->
                        val code = languages[label] ?: "en"
                        selectedLanguage = code
                        appLocale.setLocale(code)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
            }

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}


