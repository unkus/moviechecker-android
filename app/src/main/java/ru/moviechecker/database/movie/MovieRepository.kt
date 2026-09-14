package ru.moviechecker.database.movie

import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getById(id: Int): MovieEntity
    fun getAll(): List<MovieEntity>
    fun updateMovie(movie: MovieEntity)
    fun updateKinopoiskId(id: Int, kinopoiskId: String?)
    fun toggleFavoritesMark(movieId: Int)

    fun getMovieDetails(id: Int): MovieDetails

    fun getMovieCardStream(): Flow<List<MovieCard>>

    fun getNoveltiesStream(): Flow<List<MovieCard>>

    fun getExpectedStream(): Flow<List<ExpectedCard>>

}