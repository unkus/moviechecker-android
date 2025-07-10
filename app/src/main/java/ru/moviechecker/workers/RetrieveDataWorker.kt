package ru.moviechecker.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.moviechecker.database.CheckerDatabase
import ru.moviechecker.datasource.AmediaDataSource
import java.net.URI
import java.util.stream.Collectors.toList

class RetrieveDataWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val database = CheckerDatabase.getDatabase(applicationContext)
        val dataSources = listOf(AmediaDataSource())

        val errors = dataSources.parallelStream()
            .map { dataSource ->
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
            .collect(toList())

        return if (errors.isEmpty()) Result.success() else Result.failure(
            Data.Builder()
                .putStringArray("errors", errors.toTypedArray())
                .build()
        )
    }

}