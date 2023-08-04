package moviechecker.core

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import moviechecker.core.di.database.CheckerDatabase
import moviechecker.core.di.datasource.DataSource
import moviechecker.datasource.DataSourceManager
import java.time.Duration
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class DataReceiverService : Service() {

    @Inject
    lateinit var database: CheckerDatabase

    @Inject
    lateinit var dataSourceManager: DataSourceManager

    private var workRequest: WorkRequest? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.i(DataReceiverService::class.simpleName, "onCreate")
//        CoroutineScope(Dispatchers.IO).launch {
//            database.populateDatabase(dataSource.retrieveData())
//        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(DataReceiverService::class.simpleName, "onStartCommand")
        workRequest = PeriodicWorkRequestBuilder<DataRetrieveWorker>(Duration.ofMinutes(1)).build()
        workRequest?.let { WorkManager.getInstance(applicationContext).enqueue(it) }

//        startForeground(1, Notification())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(DataReceiverService::class.simpleName, "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        workRequest?.let { WorkManager.getInstance(applicationContext).cancelWorkById(it.id) }

        super.onDestroy()
    }
}