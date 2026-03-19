package com.example.choronopoets.viewmodel

import com.example.choronopoets.dataClass.Poems

data class PoemDetailUiState(
    val poem: Poems? = null,
    val isFavorite: Boolean = false,
    val isExplaining: Boolean = false,
    val explainText: String? = null,
    val explainError: String? = null,
    val error: String? = null,
)
