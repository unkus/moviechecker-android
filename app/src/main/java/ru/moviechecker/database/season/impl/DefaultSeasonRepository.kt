package ru.moviechecker.database.season.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.season.SeasonDao
import ru.moviechecker.database.season.SeasonEntity
import ru.moviechecker.database.season.SeasonWithEpisodes
import ru.moviechecker.database.season.SeasonRepository

class DefaultSeasonRepository(private val seasonDao: SeasonDao) : SeasonRepository {
    override suspend fun getSeasonsByMovieId(movieId: Int): List<SeasonEntity> = seasonDao.getSeasonsByMovieId(movieId)
    override suspend fun getSeasonsWithEpisodesByMovieId(movieId: Int): List<SeasonWithEpisodes> = seasonDao.getSeasonsWithEpisodesByMovieId(movieId)
    override fun getNumberOfSeasonsByMovieIdStream(movieId: Int): Flow<Int> = seasonDao.getNumberOfSeasonsByMovieId(movieId)

    override suspend fun updateSeason(season: SeasonEntity) = seasonDao.update(season)
}