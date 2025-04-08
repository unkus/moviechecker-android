package ru.moviechecker.database.seasons.impl

import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.seasons.SeasonDao
import ru.moviechecker.database.seasons.SeasonEntity
import ru.moviechecker.database.seasons.SeasonWithEpisodes
import ru.moviechecker.database.seasons.SeasonsRepository

class DefaultSeasonsRepository(private val seasonDao: SeasonDao) : SeasonsRepository {
    override fun getSeasonsByMovieIdStream(movieId: Int): Flow<List<SeasonEntity>> = seasonDao.getSeasonsByMovieIdStream(movieId)
    override fun getSeasonsWithEpisodesByMovieIdStream(movieId: Int): Flow<List<SeasonWithEpisodes>> = seasonDao.getSeasonsWithEpisodesByMovieIdStream(movieId)
    override fun getNumberOfSeasonsByMovieIdStream(movieId: Int): Flow<Int> = seasonDao.getNumberOfSeasonsByMovieId(movieId)

    override fun updateSeason(season: SeasonEntity) = seasonDao.update(season)
}