package ru.moviechecker.database.movies

class DefaultMoviesRepository(private val moviesDao: MovieDao) : MoviesRepository {
    override fun getById(id: Int): MovieEntity? = moviesDao.getMovieById(id)
    override fun getAll(): List<MovieEntity> = moviesDao.getMovies()
    override fun updateMovie(movie: MovieEntity) = moviesDao.update(movie)
}