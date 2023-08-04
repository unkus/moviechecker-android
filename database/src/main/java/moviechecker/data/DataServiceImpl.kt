package moviechecker.data

import android.util.Log
import moviechecker.core.di.database.DataService
import moviechecker.core.di.database.episode.Episode
import moviechecker.core.di.State
import moviechecker.core.di.datasource.DataSource
import moviechecker.data.episode.EpisodeRepositoryImpl
import moviechecker.data.favorite.FavoriteEntity
import moviechecker.data.favorite.FavoriteRepositoryImpl
import moviechecker.data.movie.MovieRepositoryImpl
import moviechecker.data.season.SeasonRepositoryImpl
import moviechecker.data.site.SiteRepositoryImpl
import moviechecker.datasource.DataSourceManager
import java.net.URI

class DataServiceImpl(
    private val database: CheckerRoomDatabase,
) : DataService {

//    private lateinit var database: CheckerRoomDatabase

    override val siteRepository by lazy { SiteRepositoryImpl(database.siteDao()) }
    override val movieRepository by lazy { MovieRepositoryImpl(database.movieDao()) }
    override val seasonRepository by lazy { SeasonRepositoryImpl(database.seasonDao()) }
    override val episodeRepository by lazy { EpisodeRepositoryImpl(database.episodeDao()) }
    override val favoriteRepository by lazy { FavoriteRepositoryImpl(database.favoriteDao()) }

    override fun cleanupData() {
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