package ru.moviechecker.database.site.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.site.SiteDao
import ru.moviechecker.database.site.SiteEntity
import ru.moviechecker.database.site.SiteRepository

class DefaultSiteRepository(private val siteDao: SiteDao) : SiteRepository {
    override suspend fun findById(id: Int): SiteEntity? = siteDao.getSiteById(id)

    override fun getByIdStream(id: Int): Flow<SiteEntity> = siteDao.getSiteByIdStream(id)

    override fun getAllStream(): Flow<List<SiteEntity>> = siteDao.getAllStream()

    override suspend fun updateSite(site: SiteEntity) = siteDao.update(site)
}