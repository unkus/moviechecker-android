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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.moviechecker.workers.RetrieveDataWorker

class RefreshViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh(onError: (String) -> Unit) {
        viewModelScope.launch {

            if (_isRefreshing.value) return@launch

            _isRefreshing.value = true

            val workManager = WorkManager.getInstance(getApplication())
            val workRequest = OneTimeWorkRequestBuilder<RetrieveDataWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresStorageNotLow(true)
                        .build()
                )
                .build()

            workManager
                .beginUniqueWork(
                    uniqueWorkName = RetrieveDataWorker.MANUAL_REFRESH,
                    existingWorkPolicy = ExistingWorkPolicy.KEEP,
                    request = workRequest
                )
                .enqueue()

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
                                        newErrors.forEach { onError(it) }
                                    }

                            } else {
                                Log.d(this.javaClass.simpleName, "Обновление закончено")
                            }

                            _isRefreshing.value = false
                        }
                    }
                }
        }
    }
}
