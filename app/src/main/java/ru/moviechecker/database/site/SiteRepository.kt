package ru.moviechecker.database.site

import kotlinx.coroutines.flow.Flow

interface SiteRepository {
    suspend fun findById(id: Int): SiteEntity?
    fun getByIdStream(id: Int): Flow<SiteEntity>
    fun getAllStream(): Flow<List<SiteEntity>>
    suspend fun updateSite(site: SiteEntity)
}