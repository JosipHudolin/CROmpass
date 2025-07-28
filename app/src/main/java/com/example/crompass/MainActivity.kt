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

        requestLocationPermissions() // 🔑 Add this line

        setContent {
            val sharedPref = getSharedPreferences("Settings", MODE_PRIVATE)
            val themePref = sharedPref.getString("theme", "system") ?: "system"
            val darkThemeState = remember { mutableStateOf<Boolean?>(null) }

            darkThemeState.value = when (themePref) {
                "light" -> false
                "dark" -> true
                else -> null
            }

            CompositionLocalProvider(
                LocalThemeState provides ThemeState(
                    isDarkTheme = darkThemeState.value == true,
                    setDarkTheme = { newValue ->
                        darkThemeState.value = newValue
                        sharedPref.edit().putString(
                            "theme",
                            when (newValue) {
                                true -> "dark"
                                false -> "light"
                                else -> "system"
                            }
                        ).apply()
                    }
                )
            ) {
                CROmpassTheme(useDarkTheme = darkThemeState.value ?: isSystemInDarkTheme()) {
                    val language = sharedPref.getString("language", Locale.getDefault().language) ?: "en"
                    val appLocaleController = remember {
                        AppLocaleController(
                            locale = Locale(language),
                            setLocale = { newLang ->
                                sharedPref.edit().putString("language", newLang).apply()
                                recreate()
                            }
                        )
                    }

                    CompositionLocalProvider(LocalAppLocale provides appLocaleController) {
                        val navController = rememberNavController()
                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notGranted = LOCATION_PERMISSIONS.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (notGranted) {
                ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, 1001)
            }
        }
    }

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
