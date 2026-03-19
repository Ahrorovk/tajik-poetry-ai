package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.PoetryRepository
import com.example.choronopoets.dao.ChatHistoryDao
import com.example.choronopoets.dao.GeneratedFavoritesDao
import com.example.choronopoets.data.remote.AiRepository
import com.example.choronopoets.dataClass.GeneratedFavorite
import com.example.choronopoets.dataClass.toEntity
import com.example.choronopoets.domain.ai.ChatMessage
import com.example.choronopoets.domain.ai.ChatRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PoetPoemGenerateViewModel(
    private val poetId: Int,
    private val aiRepository: AiRepository,
    private val poetryRepository: PoetryRepository,
    private val generatedFavoritesDao: GeneratedFavoritesDao,
    private val chatHistoryDao: ChatHistoryDao,
) : ViewModel() {

    private val _state = MutableStateFlow(PoetPoemGenerateUiState())
    val state: StateFlow<PoetPoemGenerateUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val poet = poetryRepository.getPoetById(poetId).first()
                _state.update { it.copy(poetName = poet.name) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Не удалось загрузить поэта") }
            }
        }
    }

    fun onTopicChanged(topic: String) {
        _state.update { it.copy(topic = topic, generatedPoem = null, error = null, isSaved = false) }
    }

    fun generatePoem() {
        val topic = state.value.topic.trim()
        val poetName = state.value.poetName

        if (topic.isEmpty()) {
            _state.update { it.copy(error = "Введите тему стихотворения") }
            return
        }
        if (state.value.isGenerating) return

        _state.update { it.copy(isGenerating = true, error = null, generatedPoem = null, isSaved = false) }

        val sessionKey = "poetpoem_${poetId}_${System.currentTimeMillis()}"

        viewModelScope.launch {
            try {
                val prompt = "Напиши стихотворение на тему «$topic» в стиле поэта $poetName. " +
                    "Отвечай на том же языке, на котором написана тема."
                val poem = withContext(Dispatchers.IO) { aiRepository.ask(prompt) }
                _state.update { it.copy(isGenerating = false, generatedPoem = poem) }
                withContext(Dispatchers.IO) {
                    chatHistoryDao.insertMessage(
                        ChatMessage(UUID.randomUUID().toString(), ChatRole.USER,
                            "Тема: $topic | В стиле: $poetName")
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
                        generatedPoem = null,
                        error = "Ошибка запроса: ${e.message ?: "нет соединения"}",
                    )
                }
            }
        }
    }

    fun savePoem() {
        val s = state.value
        val poem = s.generatedPoem ?: return
        if (s.isSaved) return

        viewModelScope.launch {
            generatedFavoritesDao.insert(
                GeneratedFavorite(
                    title = if (s.topic.isNotBlank()) "«${s.topic}»" else "AI стихотворение",
                    content = poem,
                    poetName = s.poetName,
                    style = "в стиле ${s.poetName}",
                )
            )
            _state.update { it.copy(isSaved = true) }
        }
    }
}
