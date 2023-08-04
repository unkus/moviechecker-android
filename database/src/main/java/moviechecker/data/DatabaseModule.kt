package moviechecker.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import moviechecker.core.di.database.CheckerDatabase
import moviechecker.core.di.database.DataService
import moviechecker.core.di.database.episode.EpisodeRepository
import moviechecker.core.di.database.favorite.FavoriteRepository
import moviechecker.core.di.database.movie.MovieRepository
import moviechecker.core.di.database.season.SeasonRepository
import moviechecker.core.di.database.site.SiteRepository
import moviechecker.data.episode.EpisodeRepositoryImpl
import moviechecker.data.favorite.FavoriteRepositoryImpl
import moviechecker.data.movie.MovieRepositoryImpl
import moviechecker.data.season.SeasonRepositoryImpl
import moviechecker.data.site.SiteRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSiteRepository(database: CheckerDatabase) : SiteRepository = SiteRepositoryImpl((database as CheckerRoomDatabase).siteDao())

    @Provides
    @Singleton
    fun provideMovieRepository(database: CheckerDatabase) : MovieRepository = MovieRepositoryImpl((database as CheckerRoomDatabase).movieDao())

    @Provides
    @Singleton
    fun provideSeasonRepository(database: CheckerDatabase) : SeasonRepository = SeasonRepositoryImpl((database as CheckerRoomDatabase).seasonDao())

    @Provides
    @Singleton
    fun provideEpisodeRepository(database: CheckerDatabase) : EpisodeRepository = EpisodeRepositoryImpl((database as CheckerRoomDatabase).episodeDao())

    @Provides
    @Singleton
    fun provideFavoriteRepository(database: CheckerDatabase) : FavoriteRepository = FavoriteRepositoryImpl((database as CheckerRoomDatabase).favoriteDao())

    @Provides
    @Singleton
    fun provideDataService(database: CheckerDatabase) : DataService = DataServiceImpl(database as CheckerRoomDatabase)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): CheckerDatabase = Room.databaseBuilder(
        appContext,
        CheckerRoomDatabase::class.java,
        "checker.db"
    ).build()
}