package ru.moviechecker.database.movies

import kotlinx.coroutines.flow.Flow

class DefaultMoviesRepository(private val moviesDao: MovieDao) : MoviesRepository {
    override fun findById(id: Int): MovieEntity? = moviesDao.getMovieById(id)
    override fun getAll(): List<MovieEntity> = moviesDao.getMovies()
    override fun updateMovie(movie: MovieEntity) = moviesDao.update(movie)

    override fun getMovieStream(): Flow<List<MovieCardsView>> = moviesDao.getMovieStream()
}