package ru.moviechecker.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.moviechecker.database.CheckerDatabase
import ru.moviechecker.datasource.AmediaDataSource
import ru.moviechecker.datasource.LostfilmDataSource
import java.net.URI

class AsyncRetrieveDataWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = CheckerDatabase.getDatabase(applicationContext)
        val dataSources = listOf(AmediaDataSource(), LostfilmDataSource())

        val errors = dataSources
            .mapNotNull { dataSource ->
                val site = database.siteDao()
                    .getSiteByMnemonic(dataSource.mnemonic)
                val address = URI.create(if(site?.useMirror == true) site.mirror else site?.address) ?: dataSource.address
                Log.i(this.javaClass.simpleName, "Получаем данные для ${dataSource.mnemonic} от $address")
                try {
                    database.populateDatabase(dataSource.retrieveData(address))
                    Log.i(this.javaClass.simpleName, "Получены данные для ${dataSource.mnemonic} от $address")
                    null
                } catch (ex: Exception) {
                    Log.w(
                        this.javaClass.simpleName,
                        "Не удалось получить данные для ${dataSource.mnemonic} от $address",
                        ex
                    )
                    "Не удалось получить данные для ${dataSource.mnemonic}"
                }
            }

        return@withContext if (errors.isEmpty()) Result.success() else Result.failure(
            Data.Builder()
                .putStringArray("errors", errors.toTypedArray())
                .build()
        )
    }

    companion object {
        const val NAME = "Проверка новых релизов"
    }

}