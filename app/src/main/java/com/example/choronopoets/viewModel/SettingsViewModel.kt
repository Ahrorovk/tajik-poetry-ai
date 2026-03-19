package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.data.AppPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(private val appPreferences: AppPreferences) : ViewModel() {

    val state = appPreferences.isDarkThemeFlow
        .map { SettingsUiState(isDarkTheme = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(isDarkTheme = appPreferences.isDarkTheme),
        )

    fun toggleTheme() {
        appPreferences.setDarkTheme(!appPreferences.isDarkTheme)
    }
}
