package com.example.moviechecker

import android.app.Application
import com.example.database.CheckerRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CheckerApplication: Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { CheckerRoomDatabase.getDatabase(this, applicationScope) }
}