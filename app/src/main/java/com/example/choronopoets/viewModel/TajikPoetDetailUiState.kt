package com.example.choronopoets.viewmodel

import com.example.choronopoets.domain.repositories.TajikPoet

data class TajikPoetDetailUiState(
    val poet: TajikPoet? = null,
    val funFact: String? = null,
    val isFunFactLoading: Boolean = false,
    val funFactError: String? = null,
)
