package ru.moviechecker.database.episode

import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    fun getAllStream(): Flow<List<EpisodeEntity>>
    fun getByIdStream(id: Int): Flow<EpisodeEntity>

    fun getEpisodesBySeasonIdStream(seasonId: Int): Flow<List<EpisodeEntity>>
    fun getEpisodesBySeasonIdStream(seasonIds: List<Int>): Flow<List<EpisodeEntity>>

    fun getById(id: Int): EpisodeEntity

    fun insertEpisode(episode: EpisodeEntity)
    fun updateEpisode(episode: EpisodeEntity)
    fun deleteEpisode(episode: EpisodeEntity)
    fun updateEpisodeState(episodeId: Int, newState: EpisodeState)
}