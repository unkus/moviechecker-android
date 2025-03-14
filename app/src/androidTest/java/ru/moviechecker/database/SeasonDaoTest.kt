package ru.moviechecker.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.moviechecker.database.CheckerDatabase
import ru.moviechecker.database.seasons.SeasonDao
import ru.moviechecker.database.seasons.SeasonEntity

@RunWith(AndroidJUnit4::class)
class SeasonDaoTest {

    private lateinit var seasonDao: SeasonDao
    private lateinit var checkerDatabase: CheckerDatabase

    val season1 = SeasonEntity(1, 1, 1, "season1")
    val season2 = SeasonEntity(2, 1, 2, "season2")
    val season3 = SeasonEntity(3, 1, 3, "season3")

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        checkerDatabase = Room.inMemoryDatabaseBuilder(context, CheckerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        seasonDao = checkerDatabase.seasonDao()
    }

    @After
    fun closeDb() {
        checkerDatabase.close()
    }

    @Test
    fun daoGetSeasonByMovieIdAndNumber_returnsSeasonFromDB() = runBlocking {
        seasonDao.insert(season1, season2, season3)
        seasonDao.getSeasonByMovieIdAndNumber(2, 2)?.let {
            assertEquals(season2, it)
        } ?: fail("Season not found")
    }

}