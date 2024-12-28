package ru.moviechecker.database.seasons

import kotlinx.coroutines.flow.Flow

class DefaultSeasonsRepository(private val seasonsDao: SeasonDao) : SeasonsRepository {
    override fun getSeasonsByMovieIdStream(movieId: Int): Flow<List<SeasonEntity>> = seasonsDao.getSeasons(movieId)
    override fun updateSeason(season: SeasonEntity) = seasonsDao.update(season)
}