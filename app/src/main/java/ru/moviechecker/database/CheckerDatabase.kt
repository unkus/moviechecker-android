package ru.moviechecker.database

import android.content.Context
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ru.moviechecker.database.episodes.EpisodeDao
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.movies.MovieDao
import ru.moviechecker.database.movies.MovieEntity
import ru.moviechecker.database.seasons.SeasonDao
import ru.moviechecker.database.seasons.SeasonEntity
import ru.moviechecker.database.sites.SiteDao
import ru.moviechecker.database.sites.SiteEntity
import ru.moviechecker.datasource.model.EpisodeData
import ru.moviechecker.datasource.model.MovieData
import ru.moviechecker.datasource.model.SeasonData
import ru.moviechecker.datasource.model.SiteData
import ru.moviechecker.datasource.model.SourceData
import java.net.SocketTimeoutException
import java.net.URI

@Database(
    entities = [SiteEntity::class, MovieEntity::class, SeasonEntity::class, EpisodeEntity::class],
    views = [],
    version = 10,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = Ver1To2AutoMigration::class),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9, Ver8To9AutoMigration::class),
        AutoMigration(from = 9, to = 10),
    ]
)
@TypeConverters(Converters::class)
abstract class CheckerDatabase : RoomDatabase() {

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
                Room.databaseBuilder(appContext, CheckerDatabase::class.java, "checker_db")
                    // Подгружает данные из файла
//                    .createFromAsset("checker_db.db")
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

    fun populateDatabase(sourceData: SourceData) {
        Log.d(this.javaClass.simpleName, "Получено ${sourceData.entries.size} записей")
        runInTransaction {
            val siteEntity = processSiteData(siteDao(), sourceData.site)
            sourceData.entries.forEach { record ->
                val siteUri = URI.create(if (siteEntity.useMirror) siteEntity.mirror else siteEntity.address)
                val movieEntity =
                    processMovieData(movieDao(),
                        siteEntity.id,
                        siteUri,
                        record.movie)
                record.season?.let {
                    val seasonEntity = processSeasonData(
                        seasonDao(),
                        siteUri,
                        movieEntity.id,
                        record.season
                    )
                    processEpisodeData(episodeDao(), seasonEntity.id, record.episode!!)
                }
            }
        }
    }

    private fun processSiteData(
        siteDao: SiteDao,
        siteData: SiteData
    ): SiteEntity {
        Log.d(this.javaClass.simpleName, "Обрабатываем: ${siteData.mnemonic}")
        siteDao.getSiteByMnemonic(siteData.mnemonic)?.let { entity ->
            entity.title = siteData.title
            entity.address = siteData.address.toString()
            entity.poster = entity.poster ?: siteData.posterLink?.let { data ->
                siteData.address.resolve(data).toURL()?.readBytes()
            }
            siteDao.update(entity)
        } ?: siteDao.insert(
            SiteEntity(
                mnemonic = siteData.mnemonic,
                title = siteData.title,
                poster = siteData.posterLink?.let {
                    siteData.address.resolve(it).toURL()?.readBytes()
                },
                address = siteData.address.toString()
            )
        )
        return siteDao.getSiteByMnemonic(siteData.mnemonic)!!
    }

    private fun processMovieData(
        movieDao: MovieDao,
        siteId: Int,
        siteAddress: URI,
        movieData: MovieData
    ): MovieEntity {
        Log.d(
            this.javaClass.simpleName,
            "Обрабатываем фильм: ${movieData.title}(${movieData.pageId})"
        )
        movieDao.getMovieBySiteIdAndPageId(siteId, movieData.pageId)?.let { entity ->
            entity.title = movieData.title
            entity.link = movieData.link
            entity.poster = entity.poster ?: movieData.posterLink?.let { link ->
                getPoster(siteAddress.resolve(link))
            }
            movieDao.update(entity)
        } ?: movieDao.insert(
            MovieEntity(
                siteId = siteId,
                pageId = movieData.pageId,
                title = movieData.title,
                link = movieData.link,
                poster = movieData.posterLink?.let { link ->
                    getPoster(siteAddress.resolve(link))
                },
                favoritesMark = false
            )
        )

        return movieDao.getMovieBySiteIdAndPageId(siteId, movieData.pageId)!!
    }

    private fun getPoster(link: URI): ByteArray? = runBlocking(Dispatchers.IO) {
        try {
            link.toURL()
                .openConnection()
                .apply {
                    connectTimeout = 1000
                    readTimeout = 3000
                }
                .getInputStream()
                .readBytes()
        } catch (ex: SocketTimeoutException) {
            null
        }
    }

    private fun processSeasonData(
        seasonDao: SeasonDao,
        siteAddress: URI,
        movieId: Int,
        seasonData: SeasonData
    ): SeasonEntity {
        Log.d(
            this.javaClass.simpleName,
            "Обрабатываем сезон: ${seasonData.number}"
        )
        seasonDao.getSeasonByMovieIdAndNumber(movieId, seasonData.number)?.let { entity ->
            entity.title = seasonData.title
            entity.link = seasonData.link
            entity.poster = entity.poster ?: seasonData.posterLink?.let { data ->
                siteAddress.resolve(data).toURL()?.readBytes()
            }
            seasonDao.update(entity)
        } ?: seasonDao.insert(
            SeasonEntity(
                movieId = movieId,
                number = seasonData.number,
                title = seasonData.title,
                link = seasonData.link,
                poster = seasonData.posterLink?.let { siteAddress.resolve(it).toURL()?.readBytes() }
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
            "Обрабатываем эпизод: ${episodeData.title} (${episodeData.number})"
        )
        episodeDao.getBySeasonIdAndNumber(seasonId, episodeData.number)?.let { entity ->
            entity.title = episodeData.title
            entity.link = episodeData.link
            if (entity.state != EpisodeState.VIEWED) {
                entity.state = EpisodeState.valueOf(episodeData.state.name)
            }
            entity.date = episodeData.date
            episodeDao.update(entity)
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

        // TODO: проверить удаляются ли сезоны и серии, кажется надо их удалять отдельно
    }

}