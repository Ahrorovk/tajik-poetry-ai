package com.example.choronopoets.viewmodel

sealed class PoetryUIEvent {
    data object LoadCenturies: PoetryUIEvent()
    data class LoadPoets(val centuryId: Int): PoetryUIEvent()
    data class LoadPoetDetails(val poetId: Int): PoetryUIEvent()
    data class LoadPoems(val poetId: Int): PoetryUIEvent()
    data class LoadPoemById(val poemId: Int) : PoetryUIEvent()
    data class SelectNationality(val nationality: String?): PoetryUIEvent()
    data object ToggleTheme: PoetryUIEvent()
}