package ru.moviechecker.database.movie

import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getById(id: Int): MovieEntity
    suspend fun getAll(): List<MovieEntity>
    suspend fun updateMovie(movie: MovieEntity)
    suspend fun updateKinopoiskId(id: Int, kinopoiskId: String?)
    suspend fun toggleFavoritesMark(movieId: Int)

    suspend fun getMovieDetails(id: Int): MovieDetails

    fun getMovieCardStream(): Flow<List<MovieCard>>

    fun getNoveltiesStream(): Flow<List<MovieCard>>

    fun getExpectedStream(): Flow<List<ExpectedCard>>

}