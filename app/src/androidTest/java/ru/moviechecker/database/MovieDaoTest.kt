package ru.moviechecker.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.moviechecker.database.episodes.EpisodeDao
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.movies.MovieDao
import ru.moviechecker.database.movies.MovieEntity
import ru.moviechecker.database.seasons.SeasonDao
import ru.moviechecker.database.seasons.SeasonEntity
import ru.moviechecker.database.sites.SiteDao
import ru.moviechecker.database.sites.SiteEntity
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private lateinit var siteDao: SiteDao
    private lateinit var movieDao: MovieDao
    private lateinit var seasonDao: SeasonDao
    private lateinit var episodeDao: EpisodeDao
    private lateinit var checkerDatabase: CheckerDatabase

    private val site1 = SiteEntity(
        1,
        mnemonic = "site_1",
        "http://1.site",
        title = "Сайт 1",
        poster = null,
        mirror = null
    )
    private val site2 = SiteEntity(
        2,
        mnemonic = "site_2",
        "http://2.site",
        title = "Сайт 2",
        poster = null,
        mirror = null
    )

    private val movie1 = MovieEntity(1, 1, "movie1", "title1", favoritesMark = false)
    private val movie2 = MovieEntity(
        2,
        1,
        "movie2",
        "title2",
        "link_to_movie2",
        "poster_for_movie2".encodeToByteArray(),
        favoritesMark = false
    )
    private val movie3 = MovieEntity(
        3,
        2,
        "movie3",
        "title3",
        "link_to_movie3",
        "poster_for_movie3".encodeToByteArray(),
        favoritesMark = false
    )

    private val season1 = SeasonEntity(
        1,
        1,
        1,
        link = "link_to_season1",
        poster = "poster_for_season1".encodeToByteArray()
    )
    private val season2 = SeasonEntity(2, 1, 2, link = "season2")
    private val season3 = SeasonEntity(3, 2, 1)
    private val season4 = SeasonEntity(4, 3, 1)

    private val episode1 =
        EpisodeEntity(1, 1, 1, link = "", state = EpisodeState.RELEASED, date = LocalDateTime.now())
    private val episode2 =
        EpisodeEntity(2, 1, 2, link = "", state = EpisodeState.RELEASED, date = LocalDateTime.now())
    private val episode3 =
        EpisodeEntity(3, 2, 1, link = "", state = EpisodeState.RELEASED, date = LocalDateTime.now())
    private val episode4 =
        EpisodeEntity(4, 3, 1, link = "", state = EpisodeState.RELEASED, date = LocalDateTime.now())
    private val episode5 =
        EpisodeEntity(5, 4, 1, link = "", state = EpisodeState.RELEASED, date = LocalDateTime.now())

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        checkerDatabase = Room.inMemoryDatabaseBuilder(context, CheckerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        siteDao = checkerDatabase.siteDao()
        movieDao = checkerDatabase.movieDao()
        seasonDao = checkerDatabase.seasonDao()
        episodeDao = checkerDatabase.episodeDao()

        siteDao.insert(site1, site2)
        movieDao.insert(movie1, movie2, movie3)
        seasonDao.insert(season1, season2, season3, season4)
        episodeDao.insert(episode1, episode2, episode3, episode4, episode5)
    }

    @After
    fun closeDb() {
        checkerDatabase.close()
    }

    @Test
    fun daoGetMovieBySiteIdAndPageId_returnsMovieFromDB() = runBlocking {
        movieDao.getMovieBySiteIdAndPageId(1, "movie1")?.let {
            assertEquals(movie1, it)
        } ?: Assert.fail("Movie not found")
    }

    @Test
    fun daoGetMovieById_returnsMovieFromDB() = runBlocking {
        assertNull("Some movie found but not expected", movieDao.getMovieById(4))
        assertEquals(movie2, movieDao.getMovieById(2)) ?: Assert.fail("Movie not found")
    }

    @Test
    fun daoGetMovieBySiteIdAndMovieId_returnsMovieFromDB() = runBlocking {
        movieDao.getMovieBySiteIdAndPageId(2, "movie2")?.let {
            Assert.fail("Some movie found but not expected")
        }
        movieDao.getMovieBySiteIdAndPageId(1, "movie2")?.let {
            assertEquals(movie2, it)
        } ?: Assert.fail("Movie not found")
    }

    @Test
    fun daoGetMovieDetailsByMovieId() = runBlocking {
        val movie1 = movieDao.getMovieDetails(1)
//        assertEquals(1, movie1.movie.id)
//        assertEquals("http://1.site", movie1.movie.address)
//        assertEquals( "link_to_season1", movie1.movie.link)
//        assertEquals( "poster_for_season1".encodeToByteArray().contentToString(), movie1.movie.poster.contentToString())

        val movie3 = movieDao.getMovieDetails(3)
//        assertEquals(3, movie3.movie.id)
//        assertEquals("http://2.site", movie3.movie.address)
//        assertEquals("link_to_movie3", movie3.movie.link)
//        assertEquals("poster_for_movie3".encodeToByteArray().contentToString(), movie3.movie.poster.contentToString())
    }

    @Test
    fun daoGetNewMovieCards() = runBlocking {
        var cards = movieDao.getMovieCardStream().first()

        assertEquals(3, cards.size)
        val card1 = cards[0]
        assertEquals(movie1.id, card1.id)
        assertEquals(movie1.favoritesMark, card1.favoritesMark)
        assertEquals(episode1.state == EpisodeState.VIEWED, card1.episode.viewedMark)
        assertEquals(season1.poster.contentToString(), card1.poster.contentToString())

        val card2 = cards[1]
        assertEquals(movie2.id, card2.id)
        assertEquals(movie2.favoritesMark, card2.favoritesMark)
        assertEquals(episode4.state == EpisodeState.VIEWED, card2.episode.viewedMark)
        assertEquals(movie2.poster.contentToString(), card2.poster.contentToString())

        val card3 = cards[2]
        assertEquals(movie3.id, card3.id)
        assertEquals(movie3.favoritesMark, card3.favoritesMark)
        assertEquals(episode5.state == EpisodeState.VIEWED, card3.episode.viewedMark)
        assertEquals(movie3.poster.contentToString(), card3.poster.contentToString())
    }
}