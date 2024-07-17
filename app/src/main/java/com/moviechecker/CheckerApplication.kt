package com.moviechecker

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.moviechecker.database.AppContainer
import com.moviechecker.database.AppDataContainer

class CheckerApplication : Application(), Configuration.Provider {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
//            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}