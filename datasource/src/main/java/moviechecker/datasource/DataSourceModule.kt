package moviechecker.datasource

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(value = [SingletonComponent::class])
class DataSourceModule {

    @Provides
    @Singleton
    fun provideDataSourceManager(): DataSourceManager = DataSourceManager()

}