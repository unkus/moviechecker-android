package ru.moviechecker.database.episodes

import kotlinx.coroutines.flow.Flow

class OfflineEpisodesRepository(private val episodeDao: EpisodeDao) : EpisodesRepository {
    override fun getAllStream(): Flow<List<EpisodeEntity>> = episodeDao.getAllEpisodesStream()
    override fun getByIdStream(id: Int): Flow<EpisodeEntity> = episodeDao.getEpisodeById(id)

    override fun getAllEpisodesViewStream(): Flow<List<IEpisodeView>> = episodeDao.getAllEpisodesViewStream()
    override fun getExpectedEpisodesViewStream(): Flow<List<IEpisodeView>> = episodeDao.getExpectedEpisodesViewStream()
    override fun getReleasedEpisodesViewStream(): Flow<List<IEpisodeView>> = episodeDao.getReleasedEpisodesViewStream()

    override suspend fun getById(id: Int): EpisodeEntity? = episodeDao.getById(id)

    override suspend fun insertEpisode(episode: EpisodeEntity) = episodeDao.insert(episode)
    override suspend fun updateEpisode(episode: EpisodeEntity) = episodeDao.update(episode)
    override suspend fun deleteEpisode(episode: EpisodeEntity) = episodeDao.delete(episode)
}