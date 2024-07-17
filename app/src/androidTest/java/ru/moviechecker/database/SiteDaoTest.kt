package ru.moviechecker.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ru.moviechecker.database.CheckerDatabase
import ru.moviechecker.database.sites.SiteDao
import ru.moviechecker.database.sites.SiteEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URI

@RunWith(AndroidJUnit4::class)
class SiteDaoTest {

    private lateinit var siteDao: SiteDao
    private lateinit var checkerDatabase: CheckerDatabase

    val site1 = SiteEntity(1, URI.create("http://site1"))
    val site2 = SiteEntity(2, URI.create("http://site2"))
    val site3 = SiteEntity(3, URI.create("http://site3"))

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        checkerDatabase = Room.inMemoryDatabaseBuilder(context, CheckerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        siteDao = checkerDatabase.siteDao()
    }

    @After
    fun closeDb() {
        checkerDatabase.close()
    }

    @Test
    fun daoGetSiteByAddress_returnsSiteFromDB() = runBlocking {
        siteDao.insert(site1, site2, site3)
        siteDao.getSiteByAddress(URI.create("http://site2"))?.let {
            Assert.assertNotNull(it)
            Assert.assertEquals(site2, it)
        }
    }

}