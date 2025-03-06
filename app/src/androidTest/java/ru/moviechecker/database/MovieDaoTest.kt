package ru.moviechecker.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.moviechecker.database.CheckerDatabase
import ru.moviechecker.database.movies.MovieDao
import ru.moviechecker.database.movies.MovieEntity

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private lateinit var movieDao: MovieDao
    private lateinit var checkerDatabase: CheckerDatabase

    val movie1 = MovieEntity(1, 1, "movie1", "title1")
    val movie2 = MovieEntity(2, 1, "movie2", "title2")
    val movie3 = MovieEntity(3, 1, "movie3", "title3")

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        checkerDatabase = Room.inMemoryDatabaseBuilder(context, CheckerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        movieDao = checkerDatabase.movieDao()
    }

    @After
    fun closeDb() {
        checkerDatabase.close()
    }

    @Test
    fun daoGetMovieBySiteIdAndPageId_returnsMovieFromDB() = runBlocking {
        movieDao.insert(movie1, movie2, movie3)
        movieDao.getMovieBySiteIdAndPageId(1, "movie1")?.let {
            assertEquals(movie1, it)
        } ?: fail("Movie not found")
    }

    @Test
    fun daoGetMovieById_returnsMovieFromDB() = runBlocking {
        movieDao.insert(movie1, movie2, movie3)
        movieDao.getMovieById(4)?.let {
            assertNull("Some movie found but not expected", it)
        }
        movieDao.getMovieById(2)?.let {
            assertEquals(movie2, it)
        } ?: fail("Movie not found")
    }

    @Test
    fun daoGetMovieBySiteIdAndMovieId_returnsMovieFromDB() = runBlocking {
        movieDao.insert(movie1, movie2, movie3)
        movieDao.getMovieBySiteIdAndPageId(2, "movie2")?.let {
            fail("Some movie found but not expected")
        }
        movieDao.getMovieBySiteIdAndPageId(1, "movie2")?.let {
            assertEquals(movie2, it)
        } ?: fail("Movie not found")
    }
}