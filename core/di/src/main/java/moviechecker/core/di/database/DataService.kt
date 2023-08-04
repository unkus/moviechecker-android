package moviechecker.core.di.database

import moviechecker.core.di.database.episode.Episode
import moviechecker.core.di.database.episode.EpisodeRepository
import moviechecker.core.di.database.favorite.FavoriteRepository
import moviechecker.core.di.database.movie.MovieRepository
import moviechecker.core.di.database.season.SeasonRepository
import moviechecker.core.di.database.site.SiteRepository
import java.net.URI

interface DataService {
    val siteRepository: SiteRepository
    val movieRepository: MovieRepository
    val seasonRepository: SeasonRepository
    val episodeRepository: EpisodeRepository
    val favoriteRepository: FavoriteRepository

    fun cleanupData()
    suspend fun markEpisodeViewed(episode: Episode)
    suspend fun addToFavorites(siteAddress: URI, moviePageId: String)
    suspend fun removeFromFavorites(siteAddress: URI, moviePageId: String)
}