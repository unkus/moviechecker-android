package ru.moviechecker.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.moviechecker.database.sites.SiteDao
import ru.moviechecker.database.sites.SiteEntity
import java.net.URI

@RunWith(AndroidJUnit4::class)
class SiteDaoTest {

    private lateinit var siteDao: SiteDao
    private lateinit var checkerDatabase: CheckerDatabase

    val site1 = SiteEntity(1, "site1", address = URI.create("http://site1"))
    val site2 = SiteEntity(2, "site2", address = URI.create("http://site2"))
    val site3 = SiteEntity(3, "site3", address = URI.create("http://site3"))

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
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
        siteDao.getSiteByMnemonic("site2")?.let {
            assertNotNull(it)
            assertEquals(site2, it)
        } ?: fail("Site not found")
    }

}