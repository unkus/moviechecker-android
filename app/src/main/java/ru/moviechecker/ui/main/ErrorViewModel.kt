package ru.moviechecker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ErrorViewModel : ViewModel() {
    private val _errorEvent = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val errorEvent = _errorEvent.asSharedFlow()

    fun triggerError(message: String) {
        viewModelScope.launch {
            _errorEvent.emit(message)
        }
    }
}