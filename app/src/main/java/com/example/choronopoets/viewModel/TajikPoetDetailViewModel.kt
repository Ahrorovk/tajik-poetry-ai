package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.data.remote.AiRepository
import com.example.choronopoets.domain.model.TajikPoetKey
import com.example.choronopoets.domain.repositories.TajikPoetsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TajikPoetDetailViewModel(
    poetKey: String,
    tajikPoetsRepository: TajikPoetsRepository,
    private val aiRepository: AiRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        TajikPoetDetailUiState(
            poet = tajikPoetsRepository.getPoetByKey(TajikPoetKey.fromId(poetKey))
        )
    )
    val state = _state.asStateFlow()

    fun loadFunFact() {
        val poetName = _state.value.poet?.name ?: return
        if (_state.value.isFunFactLoading) return
        _state.update { it.copy(isFunFactLoading = true, funFactError = null) }
        viewModelScope.launch {
            try {
                val fact = withContext(Dispatchers.IO) {
                    aiRepository.ask("Расскажи один интересный факт о поэте $poetName. Ответь на русском языке.")
                }
                _state.update { it.copy(isFunFactLoading = false, funFact = fact) }
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
