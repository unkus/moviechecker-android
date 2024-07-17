package ru.moviechecker.database.episodes

import kotlinx.coroutines.flow.Flow

interface EpisodesRepository {
    fun getAllStream(): Flow<List<EpisodeEntity>>
    fun getByIdStream(id: Int): Flow<EpisodeEntity>

    fun getAllEpisodesViewStream(): Flow<List<IEpisodeView>>
    fun getExpectedEpisodesViewStream(): Flow<List<IEpisodeView>>
    fun getReleasedEpisodesViewStream(): Flow<List<IEpisodeView>>

    suspend fun getById(id: Int): EpisodeEntity?

    suspend fun insertEpisode(episode: EpisodeEntity)
    suspend fun updateEpisode(episode: EpisodeEntity)
    suspend fun deleteEpisode(episode: EpisodeEntity)
}