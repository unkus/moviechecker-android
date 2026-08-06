package ru.moviechecker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ErrorViewModel : ViewModel() {
    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun triggerError(message: String) {
        viewModelScope.launch {
            _errorEvent.emit(message)
        }
    }
}