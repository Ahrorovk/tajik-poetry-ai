package com.example.choronopoets.viewmodel

import com.example.choronopoets.domain.ai.AiRequestType

data class ChatHistoryUiState(
    val sessions: List<ChatSessionItem> = emptyList(),
    val isLoading: Boolean = true,
    val sessionToDelete: String? = null,
)

data class ChatSessionItem(
    val sessionKey: String,
    val poetName: String,
    val modeName: String,
    val modeId: String,
    val source: String,
    val poetIdOrKey: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val messageCount: Int,
    val type: AiRequestType = AiRequestType.UNKNOWN,
)
