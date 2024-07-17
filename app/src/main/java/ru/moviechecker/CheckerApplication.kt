package ru.moviechecker

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import ru.moviechecker.database.AppContainer
import ru.moviechecker.database.AppDataContainer

class CheckerApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }

}