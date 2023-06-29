package com.example.moviechecker.model

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.moviechecker.model.episode.Episode
import com.example.moviechecker.model.episode.EpisodeDao
import com.example.moviechecker.model.episode.EpisodeDetail
import com.example.moviechecker.model.favorite.Favorite
import com.example.moviechecker.model.favorite.FavoriteDao
import com.example.moviechecker.model.favorite.FavoriteDetail
import com.example.moviechecker.model.movie.Movie
import com.example.moviechecker.model.movie.MovieDao
import com.example.moviechecker.model.season.Season
import com.example.moviechecker.model.season.SeasonDao
import com.example.moviechecker.model.site.Site
import com.example.moviechecker.model.site.SiteDao
import com.example.moviechecker.source.DataProvider
import com.example.moviechecker.source.DataRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(
    entities = [Site::class, Movie::class, Season::class, Episode::class, Favorite::class],
    views = [EpisodeDetail::class, FavoriteDetail::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CheckerRoomDatabase : RoomDatabase() {
    abstract fun siteDao(): SiteDao
    abstract fun movieDao(): MovieDao
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {

        @Volatile
        private var INSTANCE: CheckerRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CheckerRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    CheckerRoomDatabase::class.java,
                    "checker_database"
                ).addCallback(CheckerDatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun populateDatabase(records: List<DataRecord>) {
        records.forEach { record ->
            run {

                INSTANCE?.let { database ->
                    // Site
                    val site = processSiteData(database.siteDao(), record)

                    // Movie
                    val movie = processMovieData(database.movieDao(), site, record)

                    // Season
                    val season = processSeasonData(database.seasonDao(), movie, record)

                    // Episode
                    processEpisodeData(database.episodeDao(), season, record)
                }
            }
        }
    }

    private suspend fun processSiteData(
        siteDao: SiteDao,
        record: DataRecord
    ): Site {
        val siteNullable = siteDao.loadSiteByAddress(record.siteAddress)
        siteNullable?.let { site ->
            siteDao.update(site)
        } ?: run {
            siteDao.insert(Site(record.siteAddress))
        }
        return siteDao.loadSiteByAddress(record.siteAddress)!!
    }

    private suspend fun processMovieData(
        movieDao: MovieDao,
        site: Site,
        record: DataRecord
    ): Movie {
        val movieNullable = movieDao.loadMovieBySiteAndPageId(
            site.id,
            record.moviePageId
        )
        movieNullable?.let { movie ->
            movie.title = record.movieTitle
            movie.link = record.movieLink
            movieDao.update(movie)
        } ?: run {
            movieDao.insert(
                Movie(
                    site.id,
                    record.moviePageId,
                    record.movieTitle,
                    record.movieLink
                )
            )
        }
        return movieDao.loadMovieBySiteAndPageId(
            site.id,
            record.moviePageId
        )!!
    }

    private suspend fun processSeasonData(
        seasonDao: SeasonDao,
        movie: Movie,
        record: DataRecord
    ): Season {
        val seasonNullable = seasonDao.findByMovieAndNumber(
            movie.id,
            record.seasonNumber
        )
        seasonNullable?.let { season ->
            seasonDao.update(season)
        } ?: run {
            seasonDao.insert(
                Season(
                    movie.id,
                    record.seasonNumber
                )
            )
        }
        return seasonDao.findByMovieAndNumber(
            movie.id,
            record.seasonNumber
        )!!
    }

    private suspend fun processEpisodeData(
        episodeDao: EpisodeDao,
        season: Season,
        record: DataRecord
    ) {
        val episodeNullable =
            episodeDao.loadBySeasonAndNumber(
                season.id,
                record.episodeNumber
            )
        episodeNullable?.let { episode ->
            episode.title = record.episodeTitle
            episode.link = record.episodeLink
            episode.state = record.episodeState
            episode.date = record.episodeDate
            episodeDao.update(episode)
        } ?: run {
            episodeDao.insert(
                Episode(
                    season.id,
                    record.episodeNumber,
                    record.episodeTitle,
                    record.episodeLink,
                    record.episodeState,
                    record.episodeDate
                )
            )
        }
    }

    private class CheckerDatabaseCallback(private val scope: CoroutineScope) :
        Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.i("TEST", "database created")
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.i("TEST", "database opened")

            scope.launch {
                cleanupData()
                INSTANCE?.populateDatabase(DataProvider.retrieveData())
            }
        }

        suspend fun cleanupData() {
            Log.i("TEST", "cleanupData")

            INSTANCE?.episodeDao()?.loadAll()?.forEach {
                Log.i("TEST", "episode: $it")
            }

            Log.i("TEST", "удаляем все что не отмечено как избранное")
            // удаляем все что не отмечено как избранное
            INSTANCE?.movieDao()?.findNotInFavorites()?.let {movies ->
                INSTANCE?.movieDao()?.delete(movies = movies.map { it }.toTypedArray())
            }

            INSTANCE?.episodeDao()?.loadAll()?.forEach {
                Log.i("TEST", "episode: $it")
            }

            Log.i("TEST", "удаляем просмотренные эпизоды кроме последнего")
            // удаляем просмотренные эпизоды кроме последнего
            INSTANCE?.favoriteDao()?.loadAll()?.forEach {
                INSTANCE?.episodeDao()?.findViewedEpisodesWithExclusion()?.forEach {
                    Log.i("TEST", "episode: $it")
                }
            }

            INSTANCE?.movieDao()?.loadAll()?.forEach {
                Log.i("TEST", "movie: $it")
            }
            INSTANCE?.seasonDao()?.loadAll()?.forEach {
                Log.i("TEST", "season: $it")
            }
            INSTANCE?.episodeDao()?.loadAll()?.forEach {
                Log.i("TEST", "episode: $it")
            }
        }
    }
}