package ru.moviechecker.database.movies

import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    fun getById(id: Int): MovieEntity
    fun getAll(): List<MovieEntity>
    fun updateMovie(movie: MovieEntity)

    fun getMovieDetails(id: Int): MovieDetails

    fun getMovieCardStream(): Flow<List<MovieCard>>

}