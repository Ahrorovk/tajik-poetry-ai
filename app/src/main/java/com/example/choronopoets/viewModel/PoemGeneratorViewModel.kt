package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.dao.ChatHistoryDao
import com.example.choronopoets.dao.GeneratedFavoritesDao
import com.example.choronopoets.dataClass.GeneratedFavorite
import com.example.choronopoets.dataClass.toEntity
import com.example.choronopoets.domain.ai.AiService
import com.example.choronopoets.domain.ai.ChatMessage
import com.example.choronopoets.domain.ai.ChatRole
import com.example.choronopoets.domain.ai.PoemStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PoemGeneratorViewModel(
    private val aiService: AiService,
    private val generatedFavoritesDao: GeneratedFavoritesDao,
    private val chatHistoryDao: ChatHistoryDao,
) : ViewModel() {

    private val _state = MutableStateFlow(PoemGeneratorUiState())
    val state = _state.asStateFlow()

    fun onTopicChanged(topic: String) {
        _state.update { it.copy(topic = topic, poem = null, error = null, isSaved = false) }
    }

    fun onStyleChanged(style: PoemStyle) {
        _state.update { it.copy(style = style, poem = null, error = null, isSaved = false) }
    }

    fun generatePoem() {
        val topic = state.value.topic.trim()
        if (topic.isEmpty()) {
            _state.update { it.copy(error = "Введите тему стихотворения", poem = null) }
            return
        }
        if (state.value.isGenerating) return

        _state.update { it.copy(isGenerating = true, error = null, isSaved = false) }

        val sessionKey = "poemgen_${System.currentTimeMillis()}"
        val style = state.value.style

        viewModelScope.launch {
            try {
                val poem = aiService.generatePoem(topic = topic, style = style)
                _state.update { it.copy(isGenerating = false, poem = poem, error = null) }
                withContext(Dispatchers.IO) {
                    chatHistoryDao.insertMessage(
                        ChatMessage(UUID.randomUUID().toString(), ChatRole.USER,
                            "Тема: $topic | Стиль: ${style.displayName}")
                            .toEntity(sessionKey)
                    )
                    chatHistoryDao.insertMessage(
                        ChatMessage(UUID.randomUUID().toString(), ChatRole.ASSISTANT, poem)
                            .toEntity(sessionKey)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isGenerating = false,
                        poem = null,
                        error = "Ошибка запроса: ${e.message ?: "нет соединения"}",
                    )
                }
            }
        }
    }

    fun savePoem() {
        val s = state.value
        val poem = s.poem ?: return
        if (s.isSaved) return

        viewModelScope.launch {
            generatedFavoritesDao.insert(
                GeneratedFavorite(
                    title = if (s.topic.isNotBlank()) "«${s.topic}»" else "AI стихотворение",
                    content = poem,
                    poetName = "Свободный стиль",
                    style = s.style.displayName,
                )
            )
            _state.update { it.copy(isSaved = true) }
        }
    }
}
