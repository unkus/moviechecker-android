package moviechecker.core

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import moviechecker.core.di.database.CheckerDatabase
import moviechecker.datasource.DataSourceManager
import java.time.Duration
import javax.inject.Inject

@AndroidEntryPoint
class DataReceiverService : Service() {

    @Inject
    lateinit var database: CheckerDatabase

    @Inject
    lateinit var dataSourceManager: DataSourceManager

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.i(DataReceiverService::class.simpleName, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(DataReceiverService::class.simpleName, "onStartCommand")
        val workRequest = PeriodicWorkRequestBuilder<DataRetrieveWorker>(Duration.ofMinutes(1)).build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(DataReceiverService::class.simpleName, "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}