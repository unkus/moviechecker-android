package ru.moviechecker.database.site

import kotlinx.coroutines.flow.Flow

interface SiteRepository {
    fun findById(id: Int): SiteEntity?
    fun getByIdStream(id: Int): Flow<SiteEntity>
    fun getAllStream(): Flow<List<SiteEntity>>
    fun updateSite(site: SiteEntity)
}