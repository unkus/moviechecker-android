package ru.moviechecker.datasource.model

import java.net.URI

interface DataSource {
    val mnemonic: String
    val address: URI

    fun retrieveData(mirror: URI? = null): SourceData
}