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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.CompositionLocalProvider
import com.example.crompass.utils.LocalAppLocale
import com.example.crompass.utils.LocaleHelper
import java.util.Locale

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
            CROmpassTheme {
                val language = remember { mutableStateOf(Locale.getDefault().language) }
                val localeTrigger = remember { mutableStateOf(0) }

                CompositionLocalProvider(LocalAppLocale provides Locale(language.value)) {
                    val trigger = localeTrigger.value // Forces recomposition when value changes
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        onLanguageChange = { newLang ->
                            val sharedPref = getSharedPreferences("Settings", MODE_PRIVATE)
                            sharedPref.edit().putString("language", newLang).apply()
                            LocaleHelper.setLocale(this@MainActivity, newLang)
                            finish()
                            startActivity(intent)
                        }
                    )
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
