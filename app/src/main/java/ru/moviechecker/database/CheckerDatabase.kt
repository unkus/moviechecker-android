package ru.moviechecker.database

import android.content.Context
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.moviechecker.database.episodes.EpisodeDao
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodeView
import ru.moviechecker.database.movies.MovieDao
import ru.moviechecker.database.movies.MovieEntity
import ru.moviechecker.database.movies.MovieCardsView
import ru.moviechecker.database.seasons.SeasonDao
import ru.moviechecker.database.seasons.SeasonEntity
import ru.moviechecker.database.sites.SiteDao
import ru.moviechecker.database.sites.SiteEntity
import ru.moviechecker.datasource.model.DataRecord
import ru.moviechecker.datasource.model.EpisodeData
import ru.moviechecker.datasource.model.MovieData
import ru.moviechecker.datasource.model.SeasonData
import ru.moviechecker.datasource.model.SiteData

@Database(
    entities = [SiteEntity::class, MovieEntity::class, SeasonEntity::class, EpisodeEntity::class],
    views = [EpisodeView::class, MovieCardsView::class],
    version = 6,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = CheckerDatabase.Ver1To2AutoMigration::class),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6)
    ]
)
@TypeConverters(Converters::class)
abstract class CheckerDatabase : RoomDatabase() {

    @RenameColumn.Entries(
        RenameColumn(
            tableName = "movies",
            fromColumnName = "poster_link",
            toColumnName = "poster"
        ),
        RenameColumn(
            tableName = "seasons",
            fromColumnName = "poster_link",
            toColumnName = "poster"
        )
    )
    class Ver1To2AutoMigration : AutoMigrationSpec

    abstract fun siteDao(): SiteDao
    abstract fun movieDao(): MovieDao
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao

    companion object {
        @Volatile
        private var Instance: CheckerDatabase? = null

        fun getDatabase(appContext: Context): CheckerDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(appContext, CheckerDatabase::class.java, "checker.db")
                    // Раскомментировать для загрузки базы из файла
//                    .createFromAsset("checker.db")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
//                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d(this.javaClass.simpleName, "База данных создана")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.d(this.javaClass.simpleName, "База данных открыта")

//                            WorkManager.getInstance(appContext)
//                                .beginUniqueWork(
//                                    RetrieveDataWorker::class.java.simpleName,
//                                    ExistingWorkPolicy.KEEP,
//                                    OneTimeWorkRequest.from(RetrieveDataWorker::class.java)
//                                )
//                                .enqueue()
                        }
                    })
                    .build()
                    .also {
                        Instance = it
                    }
            }
        }
    }

    fun populateDatabase(records: Collection<DataRecord>) {
        Log.d(this.javaClass.simpleName, "Получено ${records.size} записей")
        records.forEach { record ->
            runInTransaction {
                val site = processSiteData(siteDao(), record.site)
                val movie = processMovieData(movieDao(), site.id, record.movie)
                val season = processSeasonData(seasonDao(), movie.id, record.season)
                processEpisodeData(episodeDao(), season.id, record.episode)
            }
        }
    }

    private fun processSiteData(
        siteDao: SiteDao,
        siteData: SiteData
    ): SiteEntity {
        Log.d(this.javaClass.simpleName, "Обрабатываем сайт: ${siteData.address}")
        siteDao.getSiteByAddress(siteData.address)?.let {
            // no data for update
            //siteDao.update(it)
        } ?: siteDao.insert(SiteEntity(address = siteData.address))
        return siteDao.getSiteByAddress(siteData.address)!!
    }

    private fun processMovieData(
        movieDao: MovieDao,
        siteId: Int,
        movieData: MovieData
    ): MovieEntity {
        Log.d(
            this.javaClass.simpleName,
            "Обрабатываем фильм: ${movieData.title}(${movieData.pageId})"
        )
        movieDao.getMovieBySiteIdAndPageId(siteId, movieData.pageId)?.let {
            it.title = movieData.title
            it.link = movieData.link
//            it.poster = movieData.posterLink?.toURL()?.readBytes()
            movieDao.update(it)
        } ?: movieDao.insert(
            MovieEntity(
                siteId = siteId,
                pageId = movieData.pageId,
                title = movieData.title,
                link = movieData.link,
                poster = movieData.posterLink?.toURL()?.readBytes()
            )
        )

        return movieDao.getMovieBySiteIdAndPageId(siteId, movieData.pageId)!!
    }

    private fun processSeasonData(
        seasonDao: SeasonDao,
        movieId: Int,
        seasonData: SeasonData
    ): SeasonEntity {
        Log.d(
            this.javaClass.simpleName,
            "Обрабатываем сезон: ${seasonData.title}(${seasonData.number})"
        )
        seasonDao.getSeasonByMovieIdAndNumber(movieId, seasonData.number)?.let {
            it.title = seasonData.title
            it.link = seasonData.link
//            it.poster = seasonData.posterLink?.toURL()?.readBytes()
            seasonDao.update(it)
        } ?: seasonDao.insert(
            SeasonEntity(
                movieId = movieId,
                number = seasonData.number,
                title = seasonData.title,
                link = seasonData.link,
                poster = seasonData.posterLink?.toURL()?.readBytes()
            )
        )

        return seasonDao.getSeasonByMovieIdAndNumber(movieId, seasonData.number)!!
    }

    private fun processEpisodeData(
        episodeDao: EpisodeDao,
        seasonId: Int,
        episodeData: EpisodeData
    ) {
        Log.d(
            this.javaClass.simpleName,
            "Обрабатываем эпизод: ${episodeData.title}(${episodeData.number})"
        )
        episodeDao.getBySeasonIdAndNumber(seasonId, episodeData.number)?.let {
            it.title = episodeData.title
            it.link = episodeData.link
            it.state = EpisodeState.valueOf(episodeData.state.name)
            it.date = episodeData.date
            episodeDao.update(it)
        } ?: episodeDao.insert(
            EpisodeEntity(
                seasonId = seasonId,
                number = episodeData.number,
                title = episodeData.title,
                link = episodeData.link,
                state = EpisodeState.valueOf(episodeData.state.name),
                date = episodeData.date
            )
        )
    }

    fun cleanupData() {
        // удаляем все что не отмечено как избранное
        movieDao().getMoviesByFavoriteMark(false).forEach { movieDao().delete(it) }

        // удаляем просмотренные эпизоды кроме последнего
        Log.d(this.javaClass.simpleName, "удаляем просмотренные эпизоды кроме последнего")
        episodeDao().getByStateSortByNumberAsc(EpisodeState.VIEWED)
            // группируем по сезону
            .groupBy { it.seasonId }
            .forEach { entry ->
                val iter = entry.value.iterator()
                while (iter.hasNext()) {
                    val ep = iter.next()
                    if (iter.hasNext()) {
                        Log.d(this.javaClass.simpleName, ep.toString())
                        // удаляем если не последний
                        episodeDao().delete(ep)
                    }
                }
            }

    }

}