package moviechecker.core.di.database

import moviechecker.core.di.database.episode.Episode
import java.net.URI

interface DataService {
    suspend fun cleanupData()
    suspend fun markEpisodeViewed(episode: Episode)
    suspend fun addToFavorites(siteAddress: URI, moviePageId: String)
    suspend fun removeFromFavorites(siteAddress: URI, moviePageId: String)
}