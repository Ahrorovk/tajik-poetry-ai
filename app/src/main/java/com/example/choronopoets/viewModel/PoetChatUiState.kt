package com.example.choronopoets.viewmodel

import com.example.choronopoets.domain.ai.ChatMessage
import com.example.choronopoets.domain.ai.PromptTemplate

data class PoetChatUiState(
    val poetName: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val selectedTemplate: PromptTemplate? = null,
    val isInitialLoading: Boolean = true,
    val isGenerating: Boolean = false,
    val error: String? = null,
    val showClearConfirm: Boolean = false,
)
