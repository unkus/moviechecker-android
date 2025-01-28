package ru.moviechecker.database.seasons

import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {
    fun updateSeason(season: SeasonEntity)
    fun getSeasonsByMovieIdStream(movieId: Int): Flow<List<SeasonEntity>>
    fun getSeasonsWithEpisodesByMovieIdStream(movieId: Int): Flow<List<SeasonWithEpisodes>>
    fun getNumberOfSeasonsByMovieIdStream(movieId: Int): Flow<Int>
}