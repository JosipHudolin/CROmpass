package com.example.crompass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.crompass.ui.theme.CROmpassTheme
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.CompositionLocalProvider
import com.example.crompass.utils.LocalAppLocale
import com.example.crompass.utils.AppLocaleController
import com.example.crompass.utils.LocaleHelper
import java.util.Locale
import com.example.crompass.ui.theme.ThemeMode
import androidx.core.content.edit
import com.example.crompass.ui.theme.LocalThemeState
import com.example.crompass.ui.theme.ThemeState

class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun attachBaseContext(newBase: android.content.Context) {
        val updatedContext = LocaleHelper.setLocale(newBase)
        super.attachBaseContext(updatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestLocationPermissions()

        setContent {
            val sharedPref = getSharedPreferences("Settings", MODE_PRIVATE)
            val themePref = sharedPref.getString("theme", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
            val themeModeState = remember { mutableStateOf(ThemeMode.valueOf(themePref)) }

            val themeState = remember {
                ThemeState(
                    isDarkTheme = themeModeState.value == ThemeMode.DARK,
                    useSystemTheme = themeModeState.value == ThemeMode.SYSTEM,
                    setDarkTheme = { enabled ->
                        themeModeState.value = if (enabled) ThemeMode.DARK else ThemeMode.LIGHT
                        sharedPref.edit { putString("theme", themeModeState.value.name) }
                    },
                    setUseSystemTheme = { enabled ->
                        themeModeState.value = if (enabled) ThemeMode.SYSTEM else ThemeMode.LIGHT
                        sharedPref.edit { putString("theme", themeModeState.value.name) }
                    }
                )
            }

            CROmpassTheme(themeMode = themeModeState.value) {
                val language = sharedPref.getString("language", Locale.getDefault().language) ?: "en"
                val appLocaleController = remember {
                    AppLocaleController(
                        locale = Locale(language),
                        setLocale = { newLang ->
                            sharedPref.edit { putString("language", newLang) }
                            recreate()
                        }
                    )
                }

                CompositionLocalProvider(
                    LocalAppLocale provides appLocaleController,
                    LocalThemeState provides themeState
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        val notGranted = LOCATION_PERMISSIONS.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, 1001)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            val granted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (!granted) {
                Toast.makeText(this, "Location permission is required to use the map", Toast.LENGTH_LONG).show()
            }
        }
    }
}
