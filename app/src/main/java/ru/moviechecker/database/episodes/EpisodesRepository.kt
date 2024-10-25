package ru.moviechecker.database.episodes

import kotlinx.coroutines.flow.Flow

interface EpisodesRepository {
    fun getAllStream(): Flow<List<EpisodeEntity>>
    fun getByIdStream(id: Int): Flow<EpisodeEntity>

    fun getAllEpisodesViewStream(): Flow<List<IEpisodeView>>
    fun getExpectedEpisodesViewStream(): Flow<List<IEpisodeView>>
    fun getReleasedEpisodesViewStream(): Flow<List<IEpisodeView>>

    fun findById(id: Int): EpisodeEntity?

    fun insertEpisode(episode: EpisodeEntity)
    fun updateEpisode(episode: EpisodeEntity)
    fun deleteEpisode(episode: EpisodeEntity)
}