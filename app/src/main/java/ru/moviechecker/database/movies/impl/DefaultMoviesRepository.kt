package ru.moviechecker.database.movies.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.movies.MovieCard
import ru.moviechecker.database.movies.MovieDao
import ru.moviechecker.database.movies.MovieDetails
import ru.moviechecker.database.movies.MovieEntity
import ru.moviechecker.database.movies.MoviesRepository

class DefaultMoviesRepository(private val movieDao: MovieDao) : MoviesRepository {
    override fun getById(id: Int) = movieDao.getMovieById(id)
    override fun getAll() = movieDao.getMovies()
    override fun updateMovie(movie: MovieEntity) = movieDao.update(movie)

    override fun getMovieDetails(id: Int) =
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
                        seasons = seasons
                    )
                }
            }
        }.invoke()

    override fun getMovieCardStream(): Flow<List<MovieCard>> = movieDao.getMovieCardStream()

}