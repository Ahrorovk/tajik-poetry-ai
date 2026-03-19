package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.PoetryRepository
import com.example.choronopoets.dataClass.Poet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class PoetryViewModel(private val repository: PoetryRepository) : ViewModel() {

    private var allPoetsCashes: List<Poet> = emptyList()

    private val _state = MutableStateFlow(PoetryUIState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        PoetryUIState()
    )

    init {
        processEvent(PoetryUIEvent.LoadCenturies)
    }

    fun processEvent(event: PoetryUIEvent){
        when(event){
            is PoetryUIEvent.LoadCenturies -> loadCenturies()
            is PoetryUIEvent.LoadPoets -> loadPoets(event.centuryId)
            is PoetryUIEvent.LoadPoetDetails -> loadPoetsDetails(event.poetId)
            is PoetryUIEvent.LoadPoems -> loadPoems(event.poetId)
            is PoetryUIEvent.LoadPoemById -> loadPoemById(event.poemId)
            is PoetryUIEvent.SelectNationality -> loadSelectedNationality(event.nationality)
            is PoetryUIEvent.ToggleTheme ->
                _state.update { it.copy(isDarkMode = true) }
        }
    }

    private fun loadCenturies() {
        repository.getAllCenturies().onEach { centuryList ->
            _state.update { it.copy(centuries = centuryList) }
        }.launchIn(viewModelScope)
    }

    private fun loadPoets(centuryId: Int) {
        repository.getPoetsByCentury(centuryId).onEach { poetsList ->
            allPoetsCashes = poetsList
            _state.update { it.copy(poets = poetsList, selectedNationality = null) }
        }.launchIn(viewModelScope)
    }

    private fun loadPoetsDetails(poetId: Int) {
        repository.getPoetById(poetId).onEach { list ->
            _state.update { it.copy(selectedPoet = list) }
        }.launchIn(viewModelScope)
    }

    private fun loadPoems(poetId: Int) {
        repository.getPoemsByPoet(poetId).onEach { list ->
            _state.update { it.copy(poems = list) }
        }.launchIn(viewModelScope)
    }

    private fun loadPoemById(poemsId: Int) {
        repository.getPoemsById(poemsId).onEach { poem ->
            _state.update { it.copy(selectedPoem = poem) }
        }.launchIn(viewModelScope)
    }

    private fun loadSelectedNationality(nationality: String?) {
        val filtered = if(nationality == null) {
            allPoetsCashes
        } else {
            allPoetsCashes.filter { it.nationality == nationality }
        }
        _state.update {
            it.copy(
                poets = filtered,
                selectedNationality = nationality
            )
        }
    }
}
