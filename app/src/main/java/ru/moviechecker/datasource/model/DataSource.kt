package ru.moviechecker.datasource.model

interface DataSource {
    fun retrieveData(): Collection<DataRecord>
}