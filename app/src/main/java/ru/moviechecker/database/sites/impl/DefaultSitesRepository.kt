package ru.moviechecker.database.sites.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.sites.SiteDao
import ru.moviechecker.database.sites.SiteEntity
import ru.moviechecker.database.sites.SitesRepository

class DefaultSitesRepository(private val siteDao: SiteDao) : SitesRepository {
    override fun findById(id: Int): SiteEntity? = siteDao.getSiteById(id)

    override fun getByIdStream(id: Int): Flow<SiteEntity> = siteDao.getSiteByIdStream(id)

    override fun getAllStream(): Flow<List<SiteEntity>> = siteDao.getAllStream()

    override fun updateSite(site: SiteEntity) = siteDao.update(site)
}