package ru.moviechecker.database.movies.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.movies.MovieCardsView
import ru.moviechecker.database.movies.MovieDao
import ru.moviechecker.database.movies.MovieEntity
import ru.moviechecker.database.movies.MovieDetails
import ru.moviechecker.database.movies.MoviesRepository

class DefaultMoviesRepository(private val moviesDao: MovieDao) : MoviesRepository {
    override fun findById(id: Int): MovieEntity? = moviesDao.getMovieById(id)
    override fun getAll(): List<MovieEntity> = moviesDao.getMovies()
    override fun updateMovie(movie: MovieEntity) = moviesDao.update(movie)

    override fun getMovieCardsStream(): Flow<List<MovieCardsView>> = moviesDao.getMovieCardsStream()
    override fun getMovieWithSiteByIdStream(id: Int): Flow<MovieDetails> = moviesDao.getMovieWithSiteByIdStream(id)
}