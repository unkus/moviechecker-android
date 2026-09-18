package ru.moviechecker.database.site

import kotlinx.coroutines.flow.Flow

interface SiteRepository {
    suspend fun findById(id: Int): SiteEntity?
    fun getAllStream(): Flow<List<SiteEntity>>
    suspend fun create(entity: SiteEntity)
    suspend fun update(entity: SiteEntity)
}