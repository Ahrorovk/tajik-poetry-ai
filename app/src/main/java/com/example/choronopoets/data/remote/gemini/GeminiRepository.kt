package com.example.choronopoets.data.remote.gemini

private const val GEMINI_API_KEY = "AIzaSyDvRX0G9wwgzpRJgbweGdBVsb13-o4a4WI"

class GeminiRepository(private val api: GeminiApi) {

    suspend fun ask(prompt: String): String {
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            )
        )

        val response = api.generateContent(
            apiKey = GEMINI_API_KEY,
            request = request,
        )

        if (response.error != null) {
            throw Exception("Gemini error ${response.error.code}: ${response.error.message}")
        }

        return response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?.trim()
            ?: throw Exception("Gemini returned an empty response")
    }
}
