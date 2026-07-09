package ru.moviechecker.database.seasons.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.seasons.SeasonDao
import ru.moviechecker.database.seasons.SeasonEntity
import ru.moviechecker.database.seasons.SeasonWithEpisodes
import ru.moviechecker.database.seasons.SeasonsRepository

class DefaultSeasonsRepository(private val seasonDao: SeasonDao) : SeasonsRepository {
    override fun getSeasonsByMovieId(movieId: Int): List<SeasonEntity> = seasonDao.getSeasonsByMovieId(movieId)
    override fun getSeasonsWithEpisodesByMovieId(movieId: Int): List<SeasonWithEpisodes> = seasonDao.getSeasonsWithEpisodesByMovieId(movieId)
    override fun getNumberOfSeasonsByMovieIdStream(movieId: Int): Flow<Int> = seasonDao.getNumberOfSeasonsByMovieId(movieId)

    override fun updateSeason(season: SeasonEntity) = seasonDao.update(season)
}