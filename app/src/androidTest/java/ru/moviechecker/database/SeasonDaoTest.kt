package ru.moviechecker.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.moviechecker.database.episodes.EpisodeDao
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.seasons.SeasonDao
import ru.moviechecker.database.seasons.SeasonEntity
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class SeasonDaoTest {

    private lateinit var seasonDao: SeasonDao
    private lateinit var episodeDao: EpisodeDao
    private lateinit var checkerDatabase: CheckerDatabase

    private val season1 = SeasonEntity(1, 1, 1, "season1")
    private val season2 = SeasonEntity(2, 1, 2, "season2")
    private val season3 = SeasonEntity(3, 1, 3, "season3")

    private val episode1 =
        EpisodeEntity(1, 1, 1, "", "", EpisodeState.RELEASED, LocalDateTime.now())
    private val episode2 =
        EpisodeEntity(2, 2, 1, "", "", EpisodeState.RELEASED, LocalDateTime.now())
    private val episode3 =
        EpisodeEntity(3, 3, 1, "", "", EpisodeState.RELEASED, LocalDateTime.now())

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        checkerDatabase = Room.inMemoryDatabaseBuilder(context, CheckerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        seasonDao = checkerDatabase.seasonDao()
        episodeDao = checkerDatabase.episodeDao()
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

    @Test
    fun daoGetSeasonsWithEpisodes() = runBlocking {
        seasonDao.insert(season1, season2, season3)
        episodeDao.insert(episode1, episode2, episode3)

        val seasonOfMovie1 = seasonDao.getSeasonsWithEpisodesByMovieIdStream(1).first()
        assertEquals("Проверка что у сериала 3 сезона", 3, seasonOfMovie1.size)
        val season = seasonOfMovie1[0].season
        assertEquals("Проверка что номер сезона первый", 1, season.number)
        assertEquals("Проверка что сезон относится к первому сериалу", 1, season.movieId)
        val episodes = seasonOfMovie1[0].episodes
        assertEquals("Проверка числа эпизодов в первом сезоне", 1, episodes.size)
        val episode = episodes[0]
        assertEquals("Проверка что у первого эмипозода номер 1",1, episode.number)
        assertEquals("Проверка что эпизод из первого сезона",1, episode.seasonId)
    }

}