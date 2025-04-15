package ru.moviechecker.datasource.model

import java.net.URI

interface DataSource {
    val address: URI

    fun retrieveData(): SourceData
}