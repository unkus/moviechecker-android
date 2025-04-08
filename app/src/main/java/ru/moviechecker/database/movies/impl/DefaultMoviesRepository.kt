package ru.moviechecker.database.movies.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.movies.MovieCard2
import ru.moviechecker.database.movies.MovieCardsView
import ru.moviechecker.database.movies.MovieDao
import ru.moviechecker.database.movies.MovieEntity
import ru.moviechecker.database.movies.MovieDetails
import ru.moviechecker.database.movies.MoviesRepository

class DefaultMoviesRepository(private val movieDao: MovieDao) : MoviesRepository {
    override fun findById(id: Int): MovieEntity? = movieDao.getMovieById(id)
    override fun getAll(): List<MovieEntity> = movieDao.getMovies()
    override fun updateMovie(movie: MovieEntity) = movieDao.update(movie)

    override fun getMovieCardsStream(siteId: Int?): Flow<List<MovieCard2>> = movieDao.getMovieCardsStream(siteId)
    override fun getMovieDetailsStream(id: Int): Flow<MovieDetails> = movieDao.getMovieDetailsStream(id)
}