package ru.moviechecker.database

import android.content.Context
import ru.moviechecker.database.episodes.DefaultEpisodesRepository
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.DefaultMoviesRepository
import ru.moviechecker.database.movies.MoviesRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val episodesRepository: EpisodesRepository
    val moviesRepository: MoviesRepository
}

/**
 * [AppContainer] implementation that provides instance of [DefaultEpisodesRepository]
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    override val episodesRepository: EpisodesRepository by lazy {
        DefaultEpisodesRepository(CheckerDatabase.getDatabase(context).episodeDao())
    }

    override val moviesRepository: MoviesRepository by lazy {
        DefaultMoviesRepository(CheckerDatabase.getDatabase(context).movieDao())
    }
}