package ru.moviechecker.database.movies

import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    fun findById(id: Int): MovieEntity?
    fun getAll(): List<MovieEntity>
    fun updateMovie(movie: MovieEntity)

    fun getMovieCardsStream(siteId: Int?): Flow<List<MovieCard2>>
    fun getMovieDetailsStream(id: Int): Flow<MovieDetails>
}