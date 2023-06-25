package com.example.moviechecker.model.site

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class SiteRepository(private val siteDao: SiteDao) {
    val allSites: Flow<List<Site>> = siteDao.sites

    @WorkerThread
    suspend fun insert(site: Site) {
        siteDao.insert(site)
    }

}