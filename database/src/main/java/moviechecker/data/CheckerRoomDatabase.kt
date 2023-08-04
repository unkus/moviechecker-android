package moviechecker.data

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import moviechecker.core.di.datasource.DataRecord
import moviechecker.data.episode.EpisodeEntity
import moviechecker.data.episode.EpisodeDao
import moviechecker.data.episode.EpisodeDetailedView
import moviechecker.data.favorite.FavoriteEntity
import moviechecker.data.favorite.FavoriteDao
import moviechecker.data.favorite.FavoriteDetail
import moviechecker.data.movie.MovieEntity
import moviechecker.data.movie.MovieDao
import moviechecker.data.season.SeasonEntity
import moviechecker.data.season.SeasonDao
import moviechecker.data.site.SiteEntity
import moviechecker.data.site.SiteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import moviechecker.core.di.State
import moviechecker.core.di.database.CheckerDatabase


@Database(
    entities = [SiteEntity::class, MovieEntity::class, SeasonEntity::class, EpisodeEntity::class, FavoriteEntity::class],
    views = [EpisodeDetailedView::class, FavoriteDetail::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CheckerRoomDatabase : RoomDatabase(), CheckerDatabase {

    abstract fun siteDao(): SiteDao
    abstract fun movieDao(): MovieDao
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun favoriteDao(): FavoriteDao

    override suspend fun populateDatabase(records: Collection<DataRecord>) {
        records.forEach { record ->
            // Site
            val site = processSiteData(siteDao(), record)
            // Movie
            val movie = processMovieData(movieDao(), site, record)
            // Season
            val season = processSeasonData(seasonDao(), movie, record)
            // Episode
            processEpisodeData(episodeDao(), season, record)
        }
    }

    private fun processSiteData(
        siteDao: SiteDao,
        record: DataRecord
    ): SiteEntity {
        val siteNullable = siteDao.loadSiteByAddress(record.siteAddress)
        siteNullable?.let { site ->
            siteDao.update(site)
        } ?: siteDao.insert(SiteEntity(record.siteAddress))

        return siteDao.loadSiteByAddress(record.siteAddress)!!
    }

    private fun processMovieData(
        movieDao: MovieDao,
        site: SiteEntity,
        record: DataRecord
    ): MovieEntity {
        val movieNullable = movieDao.loadMovieBySiteAndPageId(
            site.id,
            record.moviePageId
        )
        movieNullable?.let { movie ->
            movie.title = record.movieTitle
            movie.link = record.movieLink
            movie.posterLink = record.posterLink
            movieDao.update(movie)
        } ?: movieDao.insert(
            MovieEntity(
                site.id,
                record.moviePageId,
                record.movieTitle,
                record.movieLink,
                record.posterLink
            )
        )

        return movieDao.loadMovieBySiteAndPageId(
            site.id,
            record.moviePageId
        )!!
    }

    private fun processSeasonData(
        seasonDao: SeasonDao,
        movie: MovieEntity,
        record: DataRecord
    ): SeasonEntity {
        val seasonNullable = seasonDao.findByMovieAndNumber(
            movie.id,
            record.seasonNumber
        )
        seasonNullable?.let { season ->
            seasonDao.update(season)
        } ?: seasonDao.insert(
            SeasonEntity(
                movie.id,
                record.seasonNumber,
                record.seasonLink
            )
        )

        return seasonDao.findByMovieAndNumber(
            movie.id,
            record.seasonNumber
        )!!
    }

    private fun processEpisodeData(
        episodeDao: EpisodeDao,
        season: SeasonEntity,
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
            if (episode.state != State.VIEWED) {
                episode.state = record.episodeState
            }
            episode.date = record.episodeDate
            episodeDao.update(episode)
        } ?: episodeDao.insert(
            EpisodeEntity(
                season.id,
                record.episodeNumber,
                record.episodeTitle,
                record.episodeLink,
                record.episodeState,
                record.episodeDate
            )
        )
    }

    fun cleanupData() {
        // удаляем все что не отмечено как избранное
        movieDao().findNotInFavorites().forEach { movieDao().delete(it) }

        // удаляем просмотренные эпизоды кроме последнего
        Log.i("TEST", "удаляем просмотренные эпизоды кроме последнего")
        episodeDao().findViewedEpisodesWithExclusion()?.forEach {
            Log.i("TEST", "episode: $it")
//            INSTANCE?.episodeDao()?.delete(it)
        }

    }

    class CheckerDatabaseCallback(private val scope: CoroutineScope) :
        Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.i("TEST", "database created")
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.i("TEST", "database opened")

            scope.launch {
//                INSTANCE?.cleanupData()

//                INSTANCE?.populateDatabase(SomeDataSource.retrieveData())
//                INSTANCE?.populateDatabase(LostfilmDataSource.retrieveData())
//                DataSourceManager.sources.forEach {INSTANCE?.populateDatabase(it.retrieveData())}
//                INSTANCE?.populateDatabase(dataSource.retrieveData())
            }
        }
    }
}