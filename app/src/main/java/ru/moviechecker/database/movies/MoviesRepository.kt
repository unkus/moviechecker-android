package ru.moviechecker.database.movies

import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    fun findById(id: Int): MovieEntity?
    fun getAll(): List<MovieEntity>
    fun updateMovie(movie: MovieEntity)

    fun getMovieStream(): Flow<List<MovieCardsView>>
    fun getMovieByIdStream(id: Int): Flow<MovieEntity>
}