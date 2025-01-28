package ru.moviechecker.database.movies

import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    fun findById(id: Int): MovieEntity?
    fun getAll(): List<MovieEntity>
    fun updateMovie(movie: MovieEntity)

    fun getMovieCardsStream(): Flow<List<MovieCardsView>>
    fun getMovieDetailsStream(id: Int): Flow<MovieDetails>
    fun getMovieByIdWithSeasonsStream(id: Int): Flow<MovieWithSiteAndSeasons>
}