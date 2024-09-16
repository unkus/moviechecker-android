package ru.moviechecker.database.movies

interface MoviesRepository {
    suspend fun getById(id: Int): MovieEntity?
    suspend fun getAll(): List<MovieEntity>
    suspend fun getCount(): Int
    suspend fun updateMovie(movie: MovieEntity)
}