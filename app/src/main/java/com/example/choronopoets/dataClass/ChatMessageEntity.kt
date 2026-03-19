package com.example.choronopoets.dataClass

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.choronopoets.domain.ai.ChatMessage
import com.example.choronopoets.domain.ai.ChatRole

@Entity(
    tableName = "chat_history",
    indices = [Index(value = ["sessionKey"])],
)
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val sessionKey: String,
    val role: String,
    val content: String,
    val timestamp: Long,
)

fun ChatMessageEntity.toDomain(): ChatMessage = ChatMessage(
    id = id,
    role = ChatRole.valueOf(role),
    content = content,
    timestamp = timestamp,
)

fun ChatMessage.toEntity(sessionKey: String): ChatMessageEntity = ChatMessageEntity(
    id = id,
    sessionKey = sessionKey,
    role = role.name,
    content = content,
    timestamp = timestamp,
)
