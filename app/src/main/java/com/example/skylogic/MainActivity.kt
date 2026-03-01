package com.example.skylogic

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.skylogic.ui.theme.SkyLogicTheme
import com.example.skylogic.view.settingView.SettingsDataStore
import com.example.skylogic.view.settingView.applyLocale
import com.example.skylogic.view.weatherView.WeatherScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applyInitialLocale()

        val destination = intent?.getStringExtra("DESTINATION")

        setContent {
            SkyLogicTheme {
                WeatherScreen(startDestination = destination)
            }
        }
    }

    private fun applyInitialLocale() {
        val savedLanguage = runBlocking {
            SettingsDataStore(this@MainActivity).language.first()
        }
        val code = if (savedLanguage == "Arabic") "ar" else "en"
        applyLocale(this, code)
    }
}

