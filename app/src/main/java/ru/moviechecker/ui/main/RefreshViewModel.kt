package ru.moviechecker.ui.main

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.moviechecker.workers.AsyncRetrieveDataWorker

class RefreshViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        RefreshUiState(
            isLoading = false,
            error = null
        )
    )
    val uiState = _uiState
        .map { state ->
            RefreshUiState(
                isLoading = state.isLoading,
                error = state.error
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value
        )

    fun onRefresh() {
        _uiState.update { it.copy(isLoading = true) }

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
                                        newErrors.forEach { error -> _uiState.update { it.copy(error = error) } }
                                    }

                            } else {
                                Log.d(this.javaClass.simpleName, "Обновление закончено")
                            }
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                }
        }
    }
}

data class RefreshUiState(
    val isLoading: Boolean,
    val error: String?
)