package ru.moviechecker.database.sites

import kotlinx.coroutines.flow.Flow

interface SitesRepository {
    fun getByIdStream(id: Int): Flow<SiteEntity>
    fun getAllStream(): Flow<List<SiteEntity>>
}