package moviechecker.data.episode

import moviechecker.core.di.database.episode.EpisodeRepository

class EpisodeRepositoryImpl internal constructor(dao: EpisodeDao) : EpisodeRepository {
    override val released = dao.released
    override val expected = dao.expected
}