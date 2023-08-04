package moviechecker.data

import android.util.Log
import moviechecker.core.di.database.DataService
import moviechecker.core.di.database.episode.Episode
import moviechecker.core.di.State
import moviechecker.data.favorite.FavoriteEntity
import java.net.URI

class DataServiceImpl(
    private val database: CheckerRoomDatabase,
) : DataService {

    override suspend fun cleanupData() {
        // удаляем все что не отмечено как избранное
        database.movieDao().findNotInFavorites().let { movies ->
            database.movieDao().delete(movies = movies.map { it }.toTypedArray())
        }

        // удаляем просмотренные эпизоды кроме последнего
        Log.i("TEST", "удаляем просмотренные эпизоды кроме последнего")
        database.episodeDao().findViewedEpisodesWithExclusion()?.forEach {
            Log.i("TEST", "episode: $it")
//            INSTANCE?.episodeDao()?.delete(it)
        }
    }

    override suspend fun markEpisodeViewed(episode: Episode) {
        val episodeOpt = database.episodeDao().findBySiteAndMovieAndSeasonAndNumber(
            episode.siteAddress,
            episode.moviePageId,
            episode.seasonNumber,
            episode.number
        )
        episodeOpt?.let {
            it.state = State.VIEWED
            database.episodeDao().update(it)
        }
    }

    override suspend fun addToFavorites(siteAddress: URI, moviePageId: String) {
        val movie =
            database.movieDao().loadMovieBySiteAddressAndPageId(siteAddress, moviePageId)
        movie?.let {
            database.favoriteDao().insert(FavoriteEntity(it.id))
        }
    }

    override suspend fun removeFromFavorites(siteAddress: URI, moviePageId: String) {
        val favorite = database.favoriteDao().loadBySiteAndMovie(siteAddress, moviePageId)
        favorite?.let {
            database.favoriteDao().delete(it)
        }
    }

}