package com.example.choronopoets.viewmodel

import com.example.choronopoets.dataClass.FavoritePoem
import com.example.choronopoets.dataClass.GeneratedFavorite

data class FavoritesUiState(
    val favorites: List<FavoritePoem> = emptyList(),
    val generatedFavorites: List<GeneratedFavorite> = emptyList(),
    val isLoading: Boolean = true,
)
