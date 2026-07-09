package ru.moviechecker.ui.movie

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
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

class MoviesScreenViewModel : ViewModel() {
    private val _viewModelState = MutableStateFlow(
        MoviesUiState(
            shouldShowOnlyFavorites = false,
            shouldShowViewedEpisodes = true,
            isLoading = false
        )
    )
    private val _errors = MutableStateFlow(emptyList<String>())

    val uiState = _viewModelState
        .map { state ->
            MoviesUiState(
                shouldShowViewedEpisodes = state.shouldShowViewedEpisodes,
                shouldShowOnlyFavorites = state.shouldShowOnlyFavorites,
                isLoading = state.isLoading
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _viewModelState.value
        )

    val errors = _errors.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun toggleShouldShowOnlyFavoritesFlag() {
        _viewModelState.update {
            it.copy(
                shouldShowOnlyFavorites = !it.shouldShowOnlyFavorites
            )
        }
    }

    fun toggleShouldShowViewedEpisodesFlag() {
        _viewModelState.update {
            it.copy(
                shouldShowViewedEpisodes = !it.shouldShowViewedEpisodes
            )
        }
    }

    fun onRefresh(context: Context) {
        _viewModelState.update { it.copy(isLoading = true) }
        _errors.update { emptyList() }

        val workManager = WorkManager.getInstance(context)
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
                                        _errors.update { newErrors.asList() }
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
    val shouldShowOnlyFavorites: Boolean = false,
    val shouldShowViewedEpisodes: Boolean = true,
    val isLoading: Boolean = false
)