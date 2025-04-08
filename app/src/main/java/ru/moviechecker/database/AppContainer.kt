package ru.moviechecker.database

import android.content.Context
import ru.moviechecker.database.episodes.impl.DefaultEpisodesRepository
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.impl.DefaultMoviesRepository
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.database.seasons.impl.DefaultSeasonsRepository
import ru.moviechecker.database.seasons.SeasonsRepository
import ru.moviechecker.database.sites.SitesRepository
import ru.moviechecker.database.sites.impl.DefaultSitesRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val episodesRepository: EpisodesRepository
    val seasonsRepository: SeasonsRepository
    val moviesRepository: MoviesRepository
    val sitesRepository: SitesRepository
}

/**
 * [AppContainer] implementation that provides instance of [EpisodesRepository] and [MoviesRepository]
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    override val episodesRepository: EpisodesRepository by lazy {
        DefaultEpisodesRepository(CheckerDatabase.getDatabase(context).episodeDao())
    }

    override val seasonsRepository: SeasonsRepository by lazy {
        DefaultSeasonsRepository(CheckerDatabase.getDatabase(context).seasonDao())
    }

    override val moviesRepository: MoviesRepository by lazy {
        DefaultMoviesRepository(CheckerDatabase.getDatabase(context).movieDao())
    }

    override val sitesRepository: SitesRepository by lazy {
        DefaultSitesRepository(CheckerDatabase.getDatabase(context).siteDao())
    }
}