package moviechecker.lostfilm

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import moviechecker.core.di.datasource.DataSource
import javax.inject.Singleton

@Module
@InstallIn(value = [SingletonComponent::class])
class LostfilmModule {

    @Provides
    @Singleton
    fun provideDataSource(): DataSource = LostfilmDataSource()

}