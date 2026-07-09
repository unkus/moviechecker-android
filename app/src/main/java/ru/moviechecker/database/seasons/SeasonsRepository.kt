package ru.moviechecker.database.seasons

import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {
    fun updateSeason(season: SeasonEntity)
    fun getSeasonsByMovieId(movieId: Int): List<SeasonEntity>
    fun getSeasonsWithEpisodesByMovieId(movieId: Int): List<SeasonWithEpisodes>
    fun getNumberOfSeasonsByMovieIdStream(movieId: Int): Flow<Int>
}