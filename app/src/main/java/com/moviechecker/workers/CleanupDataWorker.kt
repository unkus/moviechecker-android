package com.moviechecker.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moviechecker.database.CheckerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CleanupDataWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = CheckerDatabase.getDatabase(applicationContext)
            database.cleanupData()
            Result.success()
        } catch (ex: Exception) {
            Log.e(this.javaClass.simpleName, "Error cleanup data", ex)
            Result.failure()
        }
    }
}