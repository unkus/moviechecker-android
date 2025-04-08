package ru.moviechecker.database.sites

import kotlinx.coroutines.flow.Flow

interface SitesRepository {
    fun getAllStream(): Flow<List<SiteEntity>>
}