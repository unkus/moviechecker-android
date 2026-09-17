package ru.moviechecker.database.season

import kotlinx.coroutines.flow.Flow

interface SeasonRepository {
    suspend fun updateSeason(season: SeasonEntity)
    suspend fun getSeasonsByMovieId(movieId: Int): List<SeasonEntity>
    suspend fun getSeasonsWithEpisodesByMovieId(movieId: Int): List<SeasonWithEpisodes>
    fun getNumberOfSeasonsByMovieIdStream(movieId: Int): Flow<Int>
}