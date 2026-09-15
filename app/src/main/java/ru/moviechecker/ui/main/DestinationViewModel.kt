package ru.moviechecker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ru.moviechecker.ui.navigation.AppDestinations

class DestinationViewModel : ViewModel() {

    private val _viewModelState = MutableStateFlow(
        DestinationUiState(
            destination = AppDestinations.CATALOG
        )
    )

    val uiState = _viewModelState
        .map { state ->
            DestinationUiState(
                destination = state.destination
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _viewModelState.value
        )

    fun changeDestination(destination: AppDestinations) {
        _viewModelState.update {
            it.copy(
                destination = destination
            )
        }
    }
}

data class DestinationUiState(
    val destination: AppDestinations
)