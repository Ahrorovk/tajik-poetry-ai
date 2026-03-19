package com.example.choronopoets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choronopoets.dao.PoemDao
import com.example.choronopoets.dataClass.Poems
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TajikPoemsViewModel(
    roomPoetId: Int,
    poemDao: PoemDao,
) : ViewModel() {

    val poems: StateFlow<List<Poems>> = poemDao
        .getPoemsByPoet(roomPoetId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
