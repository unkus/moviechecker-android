package com.moviechecker.data.source

interface DataSource {
    suspend fun retrieveData(): Collection<DataRecord>
}