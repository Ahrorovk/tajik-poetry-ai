package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.PoetryRepository
import com.example.choronopoets.domain.repositories.TajikPoetsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val poetryRepository: PoetryRepository,
    private val tajikPoetsRepository: TajikPoetsRepository,
) : ViewModel() {

    private val initialState = HomeUiState(
        isLoading = true,
        tajikPoets = tajikPoetsRepository.getAllPoets(),
    )

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<HomeUiState> = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    init {
        viewModelScope.launch {
            poetryRepository
                .getAllCenturies()
                .catch { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
                .collect { centuries ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        centuries = centuries,
                    )
                }
        }
    }
}

