package ru.moviechecker.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.moviechecker.database.CheckerDatabase

class AsyncCleanupDataWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        return@withContext try {
            val database = CheckerDatabase.getDatabase(applicationContext)
            database.cleanupData()
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(this.javaClass.simpleName, "Error cleanup data", throwable)
            Result.failure()
        }
    }
}