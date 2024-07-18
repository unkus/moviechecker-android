package ru.moviechecker

import android.app.Application
import ru.moviechecker.database.AppContainer
import ru.moviechecker.database.DefaultAppContainer

class CheckerApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}