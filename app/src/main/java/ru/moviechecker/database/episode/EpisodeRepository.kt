package ru.moviechecker.database.episode

import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    fun getAllStream(): Flow<List<EpisodeEntity>>
    fun getByIdStream(id: Int): Flow<EpisodeEntity>

    fun getEpisodesBySeasonIdStream(seasonId: Int): Flow<List<EpisodeEntity>>
    fun getEpisodesBySeasonIdStream(seasonIds: List<Int>): Flow<List<EpisodeEntity>>

    suspend fun getById(id: Int): EpisodeEntity

    suspend fun insertEpisode(episode: EpisodeEntity)
    suspend fun updateEpisode(episode: EpisodeEntity)
    suspend fun deleteEpisode(episode: EpisodeEntity)
    suspend fun updateEpisodeState(episodeId: Int, newState: EpisodeState)
}