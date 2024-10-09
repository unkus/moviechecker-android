package ru.moviechecker.database.movies

interface MoviesRepository {
    fun getById(id: Int): MovieEntity?
    fun getAll(): List<MovieEntity>
    fun updateMovie(movie: MovieEntity)
}