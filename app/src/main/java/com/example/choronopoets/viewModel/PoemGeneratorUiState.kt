package com.example.choronopoets.viewmodel

import com.example.choronopoets.domain.ai.PoemStyle

data class PoemGeneratorUiState(
    val topic: String = "",
    val style: PoemStyle = PoemStyle.ROMANTIC,
    val isGenerating: Boolean = false,
    val poem: String? = null,
    val error: String? = null,
    val isSaved: Boolean = false,
)

