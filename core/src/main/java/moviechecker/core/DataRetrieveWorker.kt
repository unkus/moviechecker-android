package moviechecker.core

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moviechecker.core.di.database.CheckerDatabase
import moviechecker.core.di.datasource.DataSource
import moviechecker.datasource.DataSourceManager

@HiltWorker
class DataRetrieveWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    val database: CheckerDatabase,
    val dataSourceManager: DataSourceManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        dataSourceManager.dataSources.forEach { database.populateDatabase(it.retrieveData()) }
        return Result.success()
    }
}