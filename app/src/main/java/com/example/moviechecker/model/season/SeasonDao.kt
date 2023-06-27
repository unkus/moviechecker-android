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
    fun loadAll(): List<Season>

    @Query("SELECT * FROM season s WHERE s.movie_id = :movie_id and s.number = :number")
    fun findByMovieAndNumber(movie_id: Int, number: Int): Season?

    @Query("SELECT * FROM season s JOIN episode e ON s.id = e.season_id")
    fun loadSeasonAndEpisodes(): Map<Season, List<Episode>>

    @Insert
    fun insert(season: Season)

    @Update
    fun update(season: Season)

    @Delete
    fun delete(season: Season)

    @Query("DELETE FROM season")
    fun deleteAll()
}