package com.example.choronopoets.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME, true))
    val isDarkThemeFlow: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    val isDarkTheme: Boolean get() = _isDarkTheme.value

    fun setDarkTheme(value: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()
        _isDarkTheme.value = value
    }

    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }
}
