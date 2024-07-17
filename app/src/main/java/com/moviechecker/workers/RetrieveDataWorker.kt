package com.moviechecker.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moviechecker.data.LostfilmDataSource
import com.moviechecker.database.CheckerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrieveDataWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(this.javaClass.simpleName, "Получаем данные")
        try {
            val database = CheckerDatabase.getDatabase(applicationContext)

            Log.d(
                this.javaClass.simpleName,
                "фильмов в базе ${database.movieDao().getMovies().size}"
            )

            database.populateDatabase(LostfilmDataSource().retrieveData())

            Result.success()
        } catch (ex: Exception) {
            Log.e(this.javaClass.simpleName, "Error receiving data: ${ex.localizedMessage}", ex)
            Result.failure()
        }
    }
}