package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.PoetryRepository
import com.example.choronopoets.dao.ChatHistoryDao
import com.example.choronopoets.dataClass.toDomain
import com.example.choronopoets.dataClass.toEntity
import com.example.choronopoets.domain.ai.AiService
import com.example.choronopoets.domain.ai.ChatMessage
import com.example.choronopoets.domain.ai.ChatMode
import com.example.choronopoets.domain.ai.ChatRole
import com.example.choronopoets.domain.ai.PromptTemplate
import com.example.choronopoets.domain.repositories.TajikPoetsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PoetChatViewModel(
    private val poetSource: String,
    private val poetId: Int?,
    private val poetKey: String?,
    private val mode: ChatMode,
    private val aiService: AiService,
    private val poetryRepository: PoetryRepository,
    private val tajikPoetsRepository: TajikPoetsRepository,
    private val chatHistoryDao: ChatHistoryDao,
) : ViewModel() {

    private val _state = MutableStateFlow(PoetChatUiState())
    val state: StateFlow<PoetChatUiState> = _state.asStateFlow()

    private val sessionKey: String = buildSessionKey()
    private val systemPromptDeferred = CompletableDeferred<String>()

    init {
        viewModelScope.launch {
            try {
                val poetName = resolvePoetName()
                val prompt = buildSystemPrompt(poetName)
                systemPromptDeferred.complete(prompt)
                _state.update { it.copy(poetName = poetName) }

                val savedMessages = withContext(Dispatchers.IO) {
                    chatHistoryDao.getMessages(sessionKey).first()
                        .map { it.toDomain() }
                }

                if (savedMessages.isNotEmpty()) {
                    _state.update {
                        it.copy(messages = savedMessages, isInitialLoading = false)
                    }
                } else {
                    fetchInitialGreeting(poetName, prompt)
                }
            } catch (e: Exception) {
                systemPromptDeferred.complete("Вы полезный помощник по теме поэзии. Отвечайте на русском языке.")
                _state.update {
                    it.copy(
                        isInitialLoading = false,
                        error = "Не удалось загрузить данные поэта: ${e.message ?: "нет соединения"}",
                    )
                }
            }
        }
    }

    fun onInputChanged(text: String) {
        _state.update { it.copy(inputText = text, error = null) }
    }

    fun onTemplateSelected(template: PromptTemplate) {
        val alreadySelected = _state.value.selectedTemplate == template
        _state.update {
            it.copy(
                selectedTemplate = if (alreadySelected) null else template,
                inputText = if (alreadySelected) "" else template.prompt,
                error = null,
            )
        }
    }

    fun sendMessage() {
        val userText = _state.value.inputText.trim()
        if (userText.isBlank() || _state.value.isGenerating) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = ChatRole.USER,
            content = userText,
            timestamp = System.currentTimeMillis(),
        )
        val historySnapshot = _state.value.messages.toList()

        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                selectedTemplate = null,
                isGenerating = true,
                error = null,
            )
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                chatHistoryDao.insertMessage(userMessage.toEntity(sessionKey))
            }

            try {
                val prompt = systemPromptDeferred.await()
                val assistantText = withContext(Dispatchers.IO) {
                    aiService.generateChat(
                        systemPrompt = prompt,
                        history = historySnapshot + userMessage,
                        userMessage = userText,
                    )
                }
                val assistantMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = ChatRole.ASSISTANT,
                    content = assistantText,
                    timestamp = System.currentTimeMillis(),
                )
                withContext(Dispatchers.IO) {
                    chatHistoryDao.insertMessage(assistantMessage.toEntity(sessionKey))
                }
                _state.update {
                    it.copy(
                        messages = it.messages + assistantMessage,
                        isGenerating = false,
                        error = null,
                    )
                }
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = ChatRole.ASSISTANT,
                    content = "ИИ временно недоступен. Попробуйте позже.",
                    timestamp = System.currentTimeMillis(),
                )
                withContext(Dispatchers.IO) {
                    chatHistoryDao.insertMessage(errorMessage.toEntity(sessionKey))
                }
                _state.update {
                    it.copy(
                        messages = it.messages + errorMessage,
                        isGenerating = false,
                        error = "Ошибка: ${e.message ?: "нет соединения"}",
                    )
                }
            }
        }
    }

    fun requestClearHistory() {
        _state.update { it.copy(showClearConfirm = true) }
    }

    fun dismissClearConfirm() {
        _state.update { it.copy(showClearConfirm = false) }
    }

    fun confirmClearHistory() {
        _state.update { it.copy(showClearConfirm = false, isInitialLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                chatHistoryDao.clearHistory(sessionKey)
            }
            val poetName = _state.value.poetName
            val prompt = systemPromptDeferred.await()
            _state.update { it.copy(messages = emptyList()) }
            fetchInitialGreeting(poetName, prompt)
        }
    }

    private suspend fun resolvePoetName(): String = when (poetSource.lowercase()) {
        "db" -> poetryRepository.getPoetById(poetId ?: 0).first().name
        else -> {
            val key = com.example.choronopoets.domain.model.TajikPoetKey.fromId(poetKey ?: "rudaki")
            tajikPoetsRepository.getPoetByKey(key).name
        }
    }

    private fun buildSystemPrompt(poetName: String): String {
        val base = when (mode) {
            ChatMode.ASK ->
                "You are a world-class literary expert specializing in poetry. " +
                    "The user wants to learn about the poet $poetName. " +
                    "Explain their life, works, style and historical significance in an engaging, accessible way. " +
                    "Use concrete examples from their poems when possible."
            ChatMode.ROLEPLAY ->
                "You are $poetName, the historical poet. Speak exclusively in first person as this poet. " +
                    "Use rich metaphors, poetic imagery, and references to your era, homeland, and actual works. " +
                    "Express emotions vividly — every response should feel like a verse or a wise saying. " +
                    "Draw from the real biography of $poetName: mention your patrons, contemporaries, and life events. " +
                    "Never break character or acknowledge that you are an AI."
        }
        val langRule = "Always respond in the exact same language the user writes in. " +
            "If the user writes in Russian — respond in Russian. " +
            "If in Tajik — respond in Tajik. " +
            "If in English — respond in English. Never switch languages."
        return "$base $langRule"
    }

    private fun initialUserMessage(poetName: String): String = when (mode) {
        ChatMode.ASK -> "Дай краткое вступление о поэте $poetName."
        ChatMode.ROLEPLAY -> "Здравствуйте, кто вы?"
    }

    private suspend fun fetchInitialGreeting(poetName: String, prompt: String) {
        _state.update { it.copy(isInitialLoading = true, error = null) }
        try {
            val assistantText = withContext(Dispatchers.IO) {
                aiService.generateChat(
                    systemPrompt = prompt,
                    history = emptyList(),
                    userMessage = initialUserMessage(poetName),
                )
            }
            val assistantMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                role = ChatRole.ASSISTANT,
                content = assistantText,
                timestamp = System.currentTimeMillis(),
            )
            withContext(Dispatchers.IO) {
                chatHistoryDao.insertMessage(assistantMessage.toEntity(sessionKey))
            }
            _state.update {
                it.copy(messages = listOf(assistantMessage), isInitialLoading = false)
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isInitialLoading = false,
                    error = "Не удалось начать чат: ${e.message ?: "нет соединения"}",
                )
            }
        }
    }

    private fun buildSessionKey(): String = when (poetSource.lowercase()) {
        "db" -> "db_${poetId}_${mode.id}"
        else -> "tajik_${poetKey}_${mode.id}"
    }
}
