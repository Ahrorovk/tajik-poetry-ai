package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.PoetryRepository
import com.example.choronopoets.dao.ChatHistoryDao
import com.example.choronopoets.domain.ai.AiRequestType
import com.example.choronopoets.domain.ai.ChatMode
import com.example.choronopoets.domain.model.TajikPoetKey
import com.example.choronopoets.domain.repositories.TajikPoetsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatHistoryViewModel(
    private val chatHistoryDao: ChatHistoryDao,
    private val poetryRepository: PoetryRepository,
    private val tajikPoetsRepository: TajikPoetsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatHistoryUiState())
    val state: StateFlow<ChatHistoryUiState> = _state.asStateFlow()

    init {
        chatHistoryDao.getAllSessions()
            .onEach { summaries ->
                val items = summaries.map { summary ->
                    val meta = resolveSessionMeta(summary.sessionKey)
                    ChatSessionItem(
                        sessionKey   = summary.sessionKey,
                        poetName     = meta.displayTitle,
                        modeName     = meta.subtitle,
                        modeId       = meta.modeId,
                        source       = meta.source,
                        poetIdOrKey  = meta.idOrKey,
                        lastMessage  = summary.lastMessage,
                        lastTimestamp = summary.lastTimestamp,
                        messageCount = summary.messageCount,
                        type         = meta.type,
                    )
                }
                _state.update { it.copy(sessions = items, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun requestDelete(sessionKey: String) {
        _state.update { it.copy(sessionToDelete = sessionKey) }
    }

    fun dismissDelete() {
        _state.update { it.copy(sessionToDelete = null) }
    }

    fun confirmDelete() {
        val key = _state.value.sessionToDelete ?: return
        _state.update { it.copy(sessionToDelete = null) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) { chatHistoryDao.clearHistory(key) }
        }
    }

    fun deleteAll() {
        val keys = _state.value.sessions.map { it.sessionKey }
        viewModelScope.launch {
            withContext(Dispatchers.IO) { keys.forEach { chatHistoryDao.clearHistory(it) } }
        }
    }

    // ── Meta resolution ───────────────────────────────────────────────────────

    private data class SessionMeta(
        val displayTitle: String,
        val subtitle: String,
        val modeId: String,
        val source: String,
        val idOrKey: String,
        val type: AiRequestType,
    )

    private suspend fun resolveSessionMeta(key: String): SessionMeta = when {

        key.startsWith("poemgen_") -> SessionMeta(
            displayTitle = "Генератор стихов",
            subtitle     = "Свободная тема",
            modeId       = "poemgen",
            source       = "ai",
            idOrKey      = "",
            type         = AiRequestType.POEM_GENERATION,
        )

        key.startsWith("poetpoem_") -> {
            // poetpoem_{poetId}_{timestamp}
            val poetId = key.split("_").getOrNull(1)?.toIntOrNull() ?: 0
            val name = resolveDbPoetName(poetId)
            SessionMeta(
                displayTitle = name,
                subtitle     = "Стих в стиле поэта",
                modeId       = "poetpoem",
                source       = "db",
                idOrKey      = poetId.toString(),
                type         = AiRequestType.POET_POEM,
            )
        }

        key.startsWith("funfact_") -> {
            // funfact_{poetId}_{timestamp}
            val poetId = key.split("_").getOrNull(1)?.toIntOrNull() ?: 0
            val name = resolveDbPoetName(poetId)
            SessionMeta(
                displayTitle = name,
                subtitle     = "Интересный факт",
                modeId       = "funfact",
                source       = "db",
                idOrKey      = poetId.toString(),
                type         = AiRequestType.FUN_FACT,
            )
        }

        key.startsWith("explain_") -> {
            // explain_{poemId}_{timestamp}
            SessionMeta(
                displayTitle = "Стихотворение",
                subtitle     = "Объяснение стиха",
                modeId       = "explain",
                source       = "poem",
                idOrKey      = key.split("_").getOrNull(1) ?: "",
                type         = AiRequestType.POEM_EXPLANATION,
            )
        }

        key.startsWith("db_") -> {
            // db_{poetId}_{modeId}
            val parts  = key.split("_", limit = 3)
            val poetId = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val modeId = parts.getOrNull(2) ?: "ask"
            val mode   = ChatMode.fromId(modeId)
            val name   = resolveDbPoetName(poetId)
            SessionMeta(
                displayTitle = name,
                subtitle     = if (mode == ChatMode.ROLEPLAY) "Чат с поэтом" else "О поэте",
                modeId       = modeId,
                source       = "db",
                idOrKey      = poetId.toString(),
                type         = if (mode == ChatMode.ROLEPLAY) AiRequestType.POET_CHAT_ROLEPLAY
                               else AiRequestType.POET_CHAT_ASK,
            )
        }

        key.startsWith("tajik_") -> {
            // tajik_{poetKey}_{modeId}
            val parts   = key.split("_", limit = 3)
            val poetKey = parts.getOrNull(1) ?: "rudaki"
            val modeId  = parts.getOrNull(2) ?: "ask"
            val mode    = ChatMode.fromId(modeId)
            val name    = resolveTajikPoetName(poetKey)
            SessionMeta(
                displayTitle = name,
                subtitle     = if (mode == ChatMode.ROLEPLAY) "Чат с поэтом" else "О поэте",
                modeId       = modeId,
                source       = "tajik",
                idOrKey      = poetKey,
                type         = if (mode == ChatMode.ROLEPLAY) AiRequestType.POET_CHAT_ROLEPLAY
                               else AiRequestType.POET_CHAT_ASK,
            )
        }

        else -> SessionMeta(
            displayTitle = "Запрос",
            subtitle     = "Прочее",
            modeId       = "",
            source       = "",
            idOrKey      = "",
            type         = AiRequestType.UNKNOWN,
        )
    }

    private suspend fun resolveDbPoetName(poetId: Int): String = withContext(Dispatchers.IO) {
        try { poetryRepository.getPoetById(poetId).first().name }
        catch (e: Exception) { "Поэт #$poetId" }
    }

    private suspend fun resolveTajikPoetName(poetKey: String): String = withContext(Dispatchers.IO) {
        try { tajikPoetsRepository.getPoetByKey(TajikPoetKey.fromId(poetKey)).name }
        catch (e: Exception) { poetKey }
    }
}
