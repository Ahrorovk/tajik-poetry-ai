package com.example.choronopoets.viewmodel

data class PoetPoemGenerateUiState(
    val poetName: String = "",
    val topic: String = "",
    val isGenerating: Boolean = false,
    val generatedPoem: String? = null,
    val error: String? = null,
    val isSaved: Boolean = false,
)
