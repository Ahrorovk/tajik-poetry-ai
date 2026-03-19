package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.dao.ChatHistoryDao
import com.example.choronopoets.dataClass.toDomain
import com.example.choronopoets.domain.ai.AiRequestType
import com.example.choronopoets.domain.ai.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class AiSessionViewerUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = true,
    val type: AiRequestType = AiRequestType.UNKNOWN,
)

class AiSessionViewerViewModel(
    private val sessionKey: String,
    private val chatHistoryDao: ChatHistoryDao,
) : ViewModel() {

    private val _state = MutableStateFlow(
        AiSessionViewerUiState(type = AiRequestType.fromSessionKey(sessionKey))
    )
    val state: StateFlow<AiSessionViewerUiState> = _state.asStateFlow()

    init {
        chatHistoryDao.getMessages(sessionKey)
            .onEach { entities ->
                _state.update {
                    it.copy(
                        messages = entities.map { e -> e.toDomain() },
                        isLoading = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
