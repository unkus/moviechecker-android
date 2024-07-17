package com.moviechecker.database.movies

class OfflineMoviesRepository(private val moviesDao: MovieDao) : MoviesRepository {
    override suspend fun getById(id: Int): MovieEntity? = moviesDao.getMovieById(id)
    override suspend fun getAll(): List<MovieEntity> = moviesDao.getMovies()

    override suspend fun updateMovie(movie: MovieEntity) = moviesDao.update(movie)
}