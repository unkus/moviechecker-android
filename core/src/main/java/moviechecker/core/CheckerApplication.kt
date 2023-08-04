package moviechecker.core

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import moviechecker.core.di.database.DataService
import javax.inject.Inject

@HiltAndroidApp
class CheckerApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var dataService: DataService

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
}