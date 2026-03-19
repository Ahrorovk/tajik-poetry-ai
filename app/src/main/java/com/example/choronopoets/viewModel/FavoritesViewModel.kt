package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.dao.FavoritesDao
import com.example.choronopoets.dao.GeneratedFavoritesDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesDao: FavoritesDao,
    private val generatedFavoritesDao: GeneratedFavoritesDao,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state = _state.asStateFlow()

    init {
        combine(
            favoritesDao.getFavorites(),
            generatedFavoritesDao.getAll(),
        ) { dbPoems, generated ->
            _state.update {
                it.copy(
                    favorites = dbPoems,
                    generatedFavorites = generated,
                    isLoading = false,
                )
            }
        }
            .catch { _state.update { it.copy(isLoading = false) } }
            .launchIn(viewModelScope)
    }

    fun removeFavorite(poemId: Int) {
        viewModelScope.launch { favoritesDao.removeFavorite(poemId) }
    }

    fun removeGeneratedFavorite(id: Int) {
        viewModelScope.launch { generatedFavoritesDao.delete(id) }
    }
}
