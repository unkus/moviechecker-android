package ru.moviechecker.database

import android.content.Context
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.episodes.OfflineEpisodesRepository
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.database.movies.OfflineMoviesRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val episodesRepository: EpisodesRepository
    val moviesRepository: MoviesRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineEpisodesRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [EpisodesRepository]
     */
    override val episodesRepository: EpisodesRepository by lazy {
        OfflineEpisodesRepository(CheckerDatabase.getDatabase(context).episodeDao())
    }

    override val moviesRepository: MoviesRepository by lazy {
        OfflineMoviesRepository(CheckerDatabase.getDatabase(context).movieDao())
    }
}