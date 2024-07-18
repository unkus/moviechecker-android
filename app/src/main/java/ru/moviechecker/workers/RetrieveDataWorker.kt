package ru.moviechecker.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.moviechecker.database.CheckerDatabase
import ru.moviechecker.datasource.LostfilmDataSource

class RetrieveDataWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(this.javaClass.simpleName, "Получаем данные")
        return@withContext try {
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