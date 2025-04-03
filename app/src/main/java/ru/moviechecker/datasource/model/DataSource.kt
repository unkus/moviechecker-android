package ru.moviechecker.datasource.model

interface DataSource {
    val site: SiteData

    fun retrieveData(): Collection<DataRecord>
}