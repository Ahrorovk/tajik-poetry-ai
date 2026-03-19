package com.example.choronopoets.viewmodel

import com.example.choronopoets.dataClass.Century
import com.example.choronopoets.domain.repositories.TajikPoet

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val centuries: List<Century> = emptyList(),
    val tajikPoets: List<TajikPoet> = emptyList(),
)

