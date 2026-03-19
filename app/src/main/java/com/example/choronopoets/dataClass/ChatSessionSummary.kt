package com.example.choronopoets.dataClass

data class ChatSessionSummary(
    val sessionKey: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val messageCount: Int,
)
