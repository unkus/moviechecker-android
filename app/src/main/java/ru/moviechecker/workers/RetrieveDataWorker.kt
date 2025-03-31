package ru.moviechecker.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.moviechecker.database.CheckerDatabase
import ru.moviechecker.datasource.AmediaDataSource
import ru.moviechecker.datasource.LostfilmDataSource

class RetrieveDataWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Log.d(this.javaClass.simpleName, "Получаем данные")
        val database = CheckerDatabase.getDatabase(applicationContext)

        return try {
            database.populateDatabase(LostfilmDataSource.site, LostfilmDataSource().retrieveData())
            database.populateDatabase(AmediaDataSource.site, AmediaDataSource().retrieveData())

            Result.success()
        } catch (ex: Exception) {
            Log.e(this.javaClass.simpleName, "Error receiving data: ${ex.localizedMessage}", ex)
            Result.failure()
        }
    }


}