package com.example.choronopoets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.choronopoets.data.AppPreferences
import com.example.choronopoets.navigation.NavigationScreen
import com.example.choronopoets.ui.theme.ChoronoPoetsTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val appPreferences: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark by appPreferences.isDarkThemeFlow.collectAsState()
            ChoronoPoetsTheme(darkTheme = isDark) {
                NavigationScreen()
            }
        }
    }
}
