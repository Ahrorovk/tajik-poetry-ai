package com.example.choronopoets.data.remote

import com.example.choronopoets.domain.ai.AiService
import com.example.choronopoets.domain.ai.ChatMessage
import com.example.choronopoets.domain.ai.ChatRole
import com.example.choronopoets.domain.ai.PoemStyle

class RetrofitAiService(
    private val aiRepository: AiRepository,
) : AiService {

    override suspend fun generateChat(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String,
    ): String {
        val prompt = buildString {
            append(systemPrompt)
            append("\n\n")

            // Conversation history (last 6 turns for context without overloading)
            history.takeLast(6).forEach { msg ->
                val role = if (msg.role == ChatRole.USER) "User" else "Assistant"
                append("$role: ${msg.content}\n")
            }

            append("User: $userMessage")

            // Language mirroring instruction appended as a hard rule at the very end
            // so the model sees it right before it begins generating the reply.
            append(
                "\n\n[CRITICAL RULE: Detect the language of the user's last message above " +
                    "and reply ONLY in that exact same language. " +
                    "If the user wrote in Russian — answer in Russian. " +
                    "If in Tajik — answer in Tajik. " +
                    "If in English — answer in English. " +
                    "Never switch to a different language under any circumstances.]"
            )
        }
        return aiRepository.ask(prompt)
    }

    override suspend fun generatePoem(
        topic: String,
        style: PoemStyle,
    ): String {
        val prompt = buildString {
            append("Write a short poem about \"$topic\" in ${style.displayName} style.")
            append(
                "\n\n[CRITICAL RULE: Write the poem in the same language as the topic text above. " +
                    "If the topic is in Russian — write in Russian. " +
                    "If in Tajik — write in Tajik. " +
                    "If in English — write in English.]"
            )
        }
        return aiRepository.ask(prompt)
    }
}
