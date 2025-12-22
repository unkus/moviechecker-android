package ru.moviechecker.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.moviechecker.database.episodes.EpisodeDao
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class EpisodeDaoTest {
    private lateinit var episodeDao: EpisodeDao
    private lateinit var checkerDatabase: CheckerDatabase

    private val episode1 = EpisodeEntity(
        1,
        1,
        1,
        "Episode #1",
        "http://movie/episode/1",
        EpisodeState.EXPECTED,
        LocalDateTime.now()
    )
    private val episode2 = EpisodeEntity(
        2,
        1,
        2,
        "Episode #2",
        "http://movie/episode/1",
        EpisodeState.RELEASED,
        LocalDateTime.now()
    )
    private val episode3 = EpisodeEntity(
        3,
        1,
        3,
        "Episode #3",
        "http://movie/episode/1",
        EpisodeState.VIEWED,
        LocalDateTime.now()
    )

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        checkerDatabase = Room.inMemoryDatabaseBuilder(context, CheckerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        episodeDao = checkerDatabase.episodeDao()
    }

    @After
    fun closeDb() {
        checkerDatabase.close()
    }

    @Test
    fun daoGetAllEpisodes_returnsAllEpisodesFromDB() = runBlocking {
        episodeDao.insert(episode1, episode2, episode3)
        episodeDao.getAllEpisodesStream().first().let {
            assertNotNull(it)
            assertEquals(3, it.size)
            assertEquals(episode1, it[0])
            assertEquals(episode2, it[1])
            assertEquals(episode3, it[2])
        }
    }

    @Test
    fun daoGetEpisodeById_getsEpisodeByIdFromDB() = runBlocking {
        episodeDao.insert(episode1, episode2, episode3)
        episodeDao.getEpisodeById(4).first().let {
            assertNull("Some episode found but not expected", it)
        }
        episodeDao.getEpisodeById(2).first().let {
            assertEquals(episode2, it)
        }
    }

    @Test
    fun daoGetEpisodeBySeasonIdAndNumber_getsEpisodeBySeasonIdAndNumberFromDB() = runBlocking {
        episodeDao.insert(episode1, episode2, episode3)
        episodeDao.getLastBySeasonId(2)?.let {
            fail("Some episode found but not expected")
        }
        episodeDao.getLastBySeasonId(1)?.let {
            assertEquals(episode2, it)
        } ?: fail("Episode not found")
    }
}