package ru.moviechecker.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.moviechecker.database.CheckerDatabase
import ru.moviechecker.datasource.AmediaDataSource
import ru.moviechecker.datasource.LostfilmDataSource

class AsyncRetrieveDataWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = CheckerDatabase.getDatabase(applicationContext)
        val dataSources = listOf(LostfilmDataSource(), AmediaDataSource())

        val errors = dataSources.mapNotNull { dataSource ->
            Log.i(this.javaClass.simpleName, "Получаем данные от ${dataSource.address}")
            try {
                database.populateDatabase(dataSource.retrieveData())
                null
            } catch (ex: Exception) {
                ex.message
            }
        }.toList()

        return@withContext if (errors.isEmpty()) Result.success() else Result.failure(
            Data.Builder()
                .putStringArray("errors", errors.toTypedArray())
                .build()
        )
    }
}