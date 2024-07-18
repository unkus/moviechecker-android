package ru.moviechecker.datasource.model

interface DataSource {
    suspend fun retrieveData(): Collection<DataRecord>
}