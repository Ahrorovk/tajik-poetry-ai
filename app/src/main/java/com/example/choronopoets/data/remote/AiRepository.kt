package com.example.choronopoets.data.remote

import com.example.choronopoets.data.remote.gemini.GeminiRepository

class AiRepository(
    private val gemini: GeminiRepository,
) {

    suspend fun ask(question: String): String {
        return try {
            gemini.ask(question)
        } catch (e: Exception) {
            throw Exception("AI временно недоступен: ${e.message}")
        }
    }
}
