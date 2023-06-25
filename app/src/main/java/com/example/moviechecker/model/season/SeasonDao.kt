package com.example.moviechecker.model.season

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moviechecker.model.episode.Episode
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @get:Query("SELECT * FROM season")
    val seasons: Flow<List<Season>>

    @Query("SELECT * FROM Season")
    suspend fun loadAll(): List<Season>

    @Query("SELECT * FROM season s WHERE s.movie_id = :movie_id and s.number = :number")
    suspend fun findByMovieAndNumber(movie_id: Int, number: Int): Season?

    @Query("SELECT * FROM season s JOIN episode e ON s.id = e.season_id")
    suspend fun loadSeasonAndEpisodes(): Map<Season, List<Episode>>

    @Insert
    suspend fun insert(season: Season)

    @Update
    suspend fun update(season: Season)

    @Delete
    suspend fun delete(season: Season)

    @Query("DELETE FROM season")
    suspend fun deleteAll()
}