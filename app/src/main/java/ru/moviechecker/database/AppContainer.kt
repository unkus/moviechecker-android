package ru.moviechecker.database

import android.content.Context
import ru.moviechecker.database.episode.EpisodeRepository
import ru.moviechecker.database.episode.impl.DefaultEpisodeRepository
import ru.moviechecker.database.movie.MovieRepository
import ru.moviechecker.database.movie.impl.DefaultMovieRepository
import ru.moviechecker.database.season.SeasonRepository
import ru.moviechecker.database.season.impl.DefaultSeasonRepository
import ru.moviechecker.database.site.SiteRepository
import ru.moviechecker.database.site.impl.DefaultSiteRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val episodeRepository: EpisodeRepository
    val seasonRepository: SeasonRepository
    val movieRepository: MovieRepository
    val siteRepository: SiteRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val episodeRepository: EpisodeRepository by lazy {
        DefaultEpisodeRepository(CheckerDatabase.getDatabase(context).episodeDao())
    }

    override val seasonRepository: SeasonRepository by lazy {
        DefaultSeasonRepository(CheckerDatabase.getDatabase(context).seasonDao())
    }

    override val movieRepository: MovieRepository by lazy {
        DefaultMovieRepository(CheckerDatabase.getDatabase(context).movieDao())
    }

    override val siteRepository: SiteRepository by lazy {
        DefaultSiteRepository(CheckerDatabase.getDatabase(context).siteDao())
    }

}