package moviechecker.core

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import moviechecker.core.di.database.CheckerDatabase
import moviechecker.datasource.DataSourceManager

@HiltWorker
class DataRetrieveWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    val database: CheckerDatabase,
    private val dataSourceManager: DataSourceManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // TODO: handle error case during data retrieval
        dataSourceManager.dataSources.forEach { database.populateDatabase(it.retrieveData()) }
        return Result.success()
    }
}