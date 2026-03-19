package com.example.choronopoets.data.remote.gemini

import com.google.gson.annotations.SerializedName

// ── Request ───────────────────────────────────────────────────────────────────

data class GeminiRequest(
    @SerializedName("contents") val contents: List<GeminiContent>,
)

data class GeminiContent(
    @SerializedName("parts") val parts: List<GeminiPart>,
)

data class GeminiPart(
    @SerializedName("text") val text: String,
)

// ── Response ──────────────────────────────────────────────────────────────────

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<GeminiCandidate>?,
    @SerializedName("error") val error: GeminiError?,
)

data class GeminiCandidate(
    @SerializedName("content") val content: GeminiContent?,
    @SerializedName("finishReason") val finishReason: String?,
)

data class GeminiError(
    @SerializedName("code") val code: Int?,
    @SerializedName("message") val message: String?,
)
