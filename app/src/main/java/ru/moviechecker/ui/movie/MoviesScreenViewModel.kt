package ru.moviechecker.ui.movie

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.moviechecker.workers.AsyncRetrieveDataWorker

class MoviesScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val _viewModelState = MutableStateFlow(
        MoviesUiState(
            isLoading = false
        )
    )
    private val _errors = MutableSharedFlow<String>()

    val uiState = _viewModelState
        .map { state ->
            MoviesUiState(
                isLoading = state.isLoading
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _viewModelState.value
        )

    val errors: SharedFlow<String> = _errors.asSharedFlow()

    fun onRefresh() {
        _viewModelState.update { it.copy(isLoading = true) }

        val workManager = WorkManager.getInstance(getApplication())
        val workRequest = OneTimeWorkRequestBuilder<AsyncRetrieveDataWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()
        workManager
            .beginUniqueWork(
                uniqueWorkName = AsyncRetrieveDataWorker.NAME,
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = workRequest
            )
            .enqueue()

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(workRequest.id)
                .collect { workInfo ->
                    Log.d(
                        this.javaClass.simpleName,
                        "Получили статус обновления: ${workInfo?.state}"
                    )
                    workInfo?.let { info ->
                        if (info.state.isFinished) {
                            if (WorkInfo.State.FAILED == info.state) {
                                info.outputData.getStringArray("errors")
                                    ?.let { newErrors ->
                                        Log.d(
                                            this.javaClass.simpleName,
                                            "Обновление закончилось с ошибкой: ${newErrors.asList()}"
                                        )
                                        newErrors.forEach { error -> _errors.emit(error) }
                                    }

                            } else {
                                Log.d(this.javaClass.simpleName, "Обновление закончено")
                            }
                            _viewModelState.update { it.copy(isLoading = false) }
                        }
                    }
                }
        }
    }
}

data class MoviesUiState(
    val isLoading: Boolean = false
)