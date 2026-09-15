package ru.moviechecker

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
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

fun CreationExtras.checkerApplication(): CheckerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CheckerApplication)