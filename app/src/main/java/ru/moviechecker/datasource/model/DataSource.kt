package ru.moviechecker.datasource.model

import java.net.URI

sealed interface DataSource {
    fun retrieveData(uri: URI): DataContainer
}

open class StrictDataSource(val mnemonic: String, address: String) : DataSource {

    val initialAddress: URI = URI.create(address)

    protected fun readContent(uri: URI): String = uri.toURL()
        .openConnection()
        .apply {
            connectTimeout = 1000
            readTimeout = 3000
        }
        .getInputStream()
        .use { it.readBytes().toString(Charsets.UTF_8) }

    override fun retrieveData(uri: URI): DataContainer {
        TODO("Not yet implemented")
    }

}