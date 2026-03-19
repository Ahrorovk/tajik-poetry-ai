package com.example.choronopoets.domain.ai

interface AiService {
    suspend fun generateChat(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String,
    ): String

    suspend fun generatePoem(
        topic: String,
        style: PoemStyle,
    ): String
}
