package ru.moviechecker.database.episodes

import kotlinx.coroutines.flow.Flow

class DefaultEpisodesRepository(private val episodeDao: EpisodeDao) : EpisodesRepository {
    override fun getAllStream(): Flow<List<EpisodeEntity>> = episodeDao.getAllEpisodesStream()
    override fun getByIdStream(id: Int): Flow<EpisodeEntity> = episodeDao.getEpisodeById(id)

    override fun getAllEpisodesViewStream(): Flow<List<IEpisodeView>> = episodeDao.getAllEpisodesViewStream()
    override fun getExpectedEpisodesViewStream(): Flow<List<IEpisodeView>> = episodeDao.getExpectedEpisodesViewStream()
    override fun getReleasedEpisodesViewStream(): Flow<List<IEpisodeView>> = episodeDao.getReleasedEpisodesViewStream()

    override fun getById(id: Int): EpisodeEntity? = episodeDao.getById(id)

    override fun insertEpisode(episode: EpisodeEntity) = episodeDao.insert(episode)
    override fun updateEpisode(episode: EpisodeEntity) = episodeDao.update(episode)
    override fun deleteEpisode(episode: EpisodeEntity) = episodeDao.delete(episode)
}