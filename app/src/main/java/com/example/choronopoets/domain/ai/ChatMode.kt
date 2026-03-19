package com.example.choronopoets.domain.ai

enum class ChatMode(val id: String) {
    ASK("ask"),
    ROLEPLAY("roleplay");

    companion object {
        fun fromId(id: String): ChatMode = when (id.lowercase()) {
            "roleplay", "role_play", "talk" -> ROLEPLAY
            else -> ASK
        }
    }
}
