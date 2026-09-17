package ru.moviechecker.database.movie.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.movie.ExpectedCard
import ru.moviechecker.database.movie.MovieCard
import ru.moviechecker.database.movie.MovieDao
import ru.moviechecker.database.movie.MovieDetails
import ru.moviechecker.database.movie.MovieEntity
import ru.moviechecker.database.movie.MovieRepository

class DefaultMovieRepository(private val movieDao: MovieDao) : MovieRepository {
    override suspend fun getById(id: Int) = movieDao.getMovieById(id)
    override suspend fun getAll() = movieDao.getMovies()
    override suspend fun updateMovie(movie: MovieEntity) = movieDao.update(movie)
    override suspend fun updateKinopoiskId(id: Int, kinopoiskId: String?) = movieDao.updateKinopoiskId(id, kinopoiskId)
    override suspend fun toggleFavoritesMark(movieId: Int) = movieDao.toggleFavoritesMark(movieId)

    override suspend fun getMovieDetails(id: Int) =
        movieDao.getMovieDetails(id).firstNotNullOf { (site, movies) ->
            {
                movies.firstNotNullOf { (movie, seasons) ->
                    MovieDetails(
                        id = movie.id,
                        siteId = site.id,
                        address = site.address,
                        pageId = movie.pageId,
                        title = movie.title,
                        link = movie.link,
                        poster = movie.poster,
                        favoritesMark = movie.favoritesMark,
                        kinopoiskId = movie.kinopoiskId,
                        seasons = seasons
                    )
                }
            }
        }.invoke()

    override fun getMovieCardStream(): Flow<List<MovieCard>> = movieDao.getMovieCardStream()

    override fun getNoveltiesStream(): Flow<List<MovieCard>> = movieDao.getNoveltiesStream()

    override fun getExpectedStream(): Flow<List<ExpectedCard>> = movieDao.getExpectedStream()
}