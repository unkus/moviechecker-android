package ru.moviechecker.database.episode.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.episode.EpisodeDao
import ru.moviechecker.database.episode.EpisodeEntity
import ru.moviechecker.database.episode.EpisodeState
import ru.moviechecker.database.episode.EpisodeRepository

class DefaultEpisodeRepository(private val episodeDao: EpisodeDao) : EpisodeRepository {
    override fun getAllStream(): Flow<List<EpisodeEntity>> = episodeDao.getAllEpisodesStream()
    override fun getByIdStream(id: Int): Flow<EpisodeEntity> = episodeDao.getEpisodeById(id)

    override fun getEpisodesBySeasonIdStream(seasonId: Int): Flow<List<EpisodeEntity>> = episodeDao.getEpisodesBySeasonId(seasonId)
    override fun getEpisodesBySeasonIdStream(seasonIds: List<Int>): Flow<List<EpisodeEntity>> = episodeDao.getEpisodesBySeasonId(seasonIds)

    override fun getById(id: Int): EpisodeEntity = episodeDao.getById(id)

    override fun insertEpisode(episode: EpisodeEntity) = episodeDao.insert(episode)
    override fun updateEpisode(episode: EpisodeEntity) = episodeDao.update(episode)
    override fun deleteEpisode(episode: EpisodeEntity) = episodeDao.delete(episode)

    override fun updateEpisodeState(episodeId: Int, newState: EpisodeState) = episodeDao.updateEpisodeState(episodeId, newState)
}