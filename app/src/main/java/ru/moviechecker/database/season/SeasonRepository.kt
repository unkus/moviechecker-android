package ru.moviechecker.database.season

import kotlinx.coroutines.flow.Flow

interface SeasonRepository {
    fun updateSeason(season: SeasonEntity)
    fun getSeasonsByMovieId(movieId: Int): List<SeasonEntity>
    fun getSeasonsWithEpisodesByMovieId(movieId: Int): List<SeasonWithEpisodes>
    fun getNumberOfSeasonsByMovieIdStream(movieId: Int): Flow<Int>
}