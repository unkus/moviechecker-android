package ru.moviechecker.database.episodes.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.episodes.EpisodeDao
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodesRepository

class DefaultEpisodesRepository(private val episodeDao: EpisodeDao) : EpisodesRepository {
    override fun getAllStream(): Flow<List<EpisodeEntity>> = episodeDao.getAllEpisodesStream()
    override fun getByIdStream(id: Int): Flow<EpisodeEntity> = episodeDao.getEpisodeById(id)

    override fun getEpisodesBySeasonIdStream(seasonId: Int): Flow<List<EpisodeEntity>> = episodeDao.getEpisodesBySeasonId(seasonId)
    override fun getEpisodesBySeasonIdStream(seasonIds: List<Int>): Flow<List<EpisodeEntity>> = episodeDao.getEpisodesBySeasonId(seasonIds)

    override fun findById(id: Int): EpisodeEntity? = episodeDao.getById(id)

    override fun insertEpisode(episode: EpisodeEntity) = episodeDao.insert(episode)
    override fun updateEpisode(episode: EpisodeEntity) = episodeDao.update(episode)
    override fun deleteEpisode(episode: EpisodeEntity) = episodeDao.delete(episode)
}