package ru.moviechecker.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.moviechecker.database.CheckerDatabase
import ru.moviechecker.datasource.AmediaDataSource
import ru.moviechecker.datasource.LostfilmDataSource

class RetrieveDataWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
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

        return if (errors.isEmpty()) Result.success() else Result.failure(
            Data.Builder()
                .putStringArray("errors", errors.toTypedArray())
                .build()
        )
    }

}