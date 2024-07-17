package com.moviechecker.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moviechecker.database.movies.MovieDao
import com.moviechecker.database.movies.MovieEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private lateinit var movieDao: MovieDao
    private lateinit var checkerDatabase: CheckerDatabase

    val movie1 = MovieEntity(1, 1, "movie1", "title1")
    val movie2 = MovieEntity(2, 1, "movie2", "title2")
    val movie3 = MovieEntity(3, 1, "movie3", "title3")

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
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
            Assert.assertEquals(movie1, it)
        }
    }

    @Test
    fun daoGetMovieById_returnsMovieFromDB() = runBlocking {
        movieDao.insert(movie1, movie2, movie3)
        movieDao.getMovieById(4)?.let {
            Assert.assertNull("Some movie found but not expected", it)
        }
        movieDao.getMovieById(2)?.let {
            Assert.assertEquals(movie2, it)
        }
    }

    @Test
    fun daoGetMovieBySiteIdAndMovieId_returnsMovieFromDB() = runBlocking {
        movieDao.insert(movie1, movie2, movie3)
        movieDao.getMovieBySiteIdAndPageId(2, "movie2")?.let {
            Assert.fail("Some movie found but not expected")
        }
        movieDao.getMovieBySiteIdAndPageId(1, "movie2")?.let {
            Assert.assertEquals(movie2, it)
        } ?: Assert.fail("Movie not found")
    }
}