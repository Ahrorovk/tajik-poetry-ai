package com.example.choronopoets.viewmodel

import com.example.choronopoets.dataClass.Poems
import com.example.choronopoets.dataClass.Poet

data class PoetDetailsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val poet: Poet? = null,
    val poems: List<Poems> = emptyList(),
    val funFact: String? = null,
    val isFunFactLoading: Boolean = false,
    val funFactError: String? = null,
)
