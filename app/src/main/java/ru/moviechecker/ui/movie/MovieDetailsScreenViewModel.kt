package ru.moviechecker.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MovieDetailsScreenViewModel : ViewModel() {

    private val _viewModelState = MutableStateFlow(
        MovieDetailsUiState(
            expandedSeasonNumber = 0
        )
    )

    val uiState = _viewModelState
        .map { state ->
            MovieDetailsUiState(
                expandedSeasonNumber = state.expandedSeasonNumber
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _viewModelState.value
        )

    fun expandSeason(seasonNumber: Int) {
        _viewModelState.update {
            it.copy(
                expandedSeasonNumber = if (it.expandedSeasonNumber == seasonNumber) 0 else seasonNumber
            )
        }
    }
}

data class MovieDetailsUiState(
    val expandedSeasonNumber: Int
)