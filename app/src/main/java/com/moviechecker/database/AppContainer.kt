package com.moviechecker.database

import android.content.Context
import com.moviechecker.database.episodes.EpisodesRepository
import com.moviechecker.database.episodes.OfflineEpisodesRepository
import com.moviechecker.database.movies.MoviesRepository
import com.moviechecker.database.movies.OfflineMoviesRepository

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