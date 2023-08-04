package moviechecker.data.movie

import moviechecker.core.di.database.movie.MovieRepository

class MovieRepositoryImpl internal constructor(dao: MovieDao) : MovieRepository {
}