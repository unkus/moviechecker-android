package moviechecker.datasource

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import moviechecker.core.di.datasource.DataSource
import javax.inject.Singleton

@Module
@InstallIn(value = [SingletonComponent::class])
class DataSourceModule {

    @Provides
    @Singleton
    fun provideDataSourceManager(datasource: DataSource): DataSourceManager = DataSourceManager()

//    @Provides
//    @Singleton
//    fun provideDataSources(): DataSource = AmediaDataSource()

}