package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.PoetryRepository
import com.example.choronopoets.dao.ChatHistoryDao
import com.example.choronopoets.data.remote.AiRepository
import com.example.choronopoets.dataClass.Poet
import com.example.choronopoets.dataClass.toEntity
import com.example.choronopoets.domain.ai.ChatMessage
import com.example.choronopoets.domain.ai.ChatRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PoetDetailsViewModel(
    private val poetId: Int,
    private val poetryRepository: PoetryRepository,
    private val aiRepository: AiRepository,
    private val chatHistoryDao: ChatHistoryDao,
) : ViewModel() {

    private val poetFlow = poetryRepository.getPoetById(poetId)
    private val poemsFlow = poetryRepository.getPoemsByPoet(poetId)

    private val _state = MutableStateFlow(PoetDetailsUiState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(poetFlow, poemsFlow) { poet: Poet, poems ->
                PoetDetailsUiState(
                    isLoading = false,
                    error = null,
                    poet = poet,
                    poems = poems,
                )
            }
                .catch { e ->
                    _state.value = PoetDetailsUiState(
                        isLoading = false,
                        error = e.message ?: "Неизвестная ошибка",
                        poet = null,
                        poems = emptyList(),
                    )
                }
                .collectLatest { uiState ->
                    _state.update { current ->
                        uiState.copy(
                            funFact = current.funFact,
                            isFunFactLoading = current.isFunFactLoading,
                            funFactError = current.funFactError,
                        )
                    }
                }
        }
    }

    fun loadFunFact() {
        val poetName = _state.value.poet?.name ?: return
        if (_state.value.isFunFactLoading) return
        _state.update { it.copy(isFunFactLoading = true, funFactError = null) }
        val sessionKey = "funfact_${poetId}_${System.currentTimeMillis()}"
        viewModelScope.launch {
            try {
                val prompt = "Расскажи один интересный факт о поэте $poetName. Ответь на русском языке."
                val fact = withContext(Dispatchers.IO) { aiRepository.ask(prompt) }
                _state.update { it.copy(isFunFactLoading = false, funFact = fact) }
                withContext(Dispatchers.IO) {
                    chatHistoryDao.insertMessage(
                        ChatMessage(UUID.randomUUID().toString(), ChatRole.USER,
                            "Интересный факт о поэте: $poetName")
                            .toEntity(sessionKey)
                    )
                    chatHistoryDao.insertMessage(
                        ChatMessage(UUID.randomUUID().toString(), ChatRole.ASSISTANT, fact)
                            .toEntity(sessionKey)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isFunFactLoading = false,
                        funFactError = "ИИ временно недоступен: ${e.message ?: "нет соединения"}",
                    )
                }
            }
        }
    }

    fun dismissFunFact() {
        _state.update { it.copy(funFact = null, funFactError = null) }
    }
}
