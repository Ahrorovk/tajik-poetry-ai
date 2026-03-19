package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.dao.ChatHistoryDao
import com.example.choronopoets.dao.FavoritesDao
import com.example.choronopoets.dao.PoemDao
import com.example.choronopoets.data.remote.AiRepository
import com.example.choronopoets.dataClass.FavoritePoem
import com.example.choronopoets.dataClass.toEntity
import com.example.choronopoets.domain.ai.ChatMessage
import com.example.choronopoets.domain.ai.ChatRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PoemDetailViewModel(
    private val poemId: Int,
    private val poemDao: PoemDao,
    private val favoritesDao: FavoritesDao,
    private val aiRepository: AiRepository,
    private val chatHistoryDao: ChatHistoryDao,
) : ViewModel() {

    private val _state = MutableStateFlow(PoemDetailUiState())
    val state = _state.asStateFlow()

    init {
        combine(
            poemDao.getPoemsById(poemId),
            favoritesDao.isFavorite(poemId),
        ) { poem, isFav ->
            _state.update { it.copy(poem = poem, isFavorite = isFav) }
        }
            .catch { e -> _state.update { it.copy(error = e.message) } }
            .launchIn(viewModelScope)
    }

    fun explainPoem() {
        val poem = _state.value.poem ?: return
        if (_state.value.isExplaining) return
        _state.update { it.copy(isExplaining = true, explainError = null) }
        val sessionKey = "explain_${poemId}_${System.currentTimeMillis()}"
        viewModelScope.launch {
            try {
                val explanation = withContext(Dispatchers.IO) {
                    aiRepository.ask(
                        "Объясни это стихотворение простыми словами на русском языке:\n\n" +
                            "Название: ${poem.title}\n\n${poem.content}"
                    )
                }
                _state.update { it.copy(isExplaining = false, explainText = explanation) }
                withContext(Dispatchers.IO) {
                    chatHistoryDao.insertMessage(
                        ChatMessage(UUID.randomUUID().toString(), ChatRole.USER,
                            "Объяснение стихотворения: «${poem.title}»")
                            .toEntity(sessionKey)
                    )
                    chatHistoryDao.insertMessage(
                        ChatMessage(UUID.randomUUID().toString(), ChatRole.ASSISTANT, explanation)
                            .toEntity(sessionKey)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isExplaining = false,
                        explainError = "ИИ временно недоступен: ${e.message ?: "нет соединения"}",
                    )
                }
            }
        }
    }

    fun dismissExplain() {
        _state.update { it.copy(explainText = null, explainError = null) }
    }

    fun toggleFavorite() {
        val poem = _state.value.poem ?: return
        viewModelScope.launch {
            if (_state.value.isFavorite) {
                favoritesDao.removeFavorite(poemId)
            } else {
                favoritesDao.addFavorite(
                    FavoritePoem(
                        poemId = poem.id,
                        poetId = poem.poetId,
                        title = poem.title,
                        content = poem.content,
                    )
                )
            }
        }
    }
}
