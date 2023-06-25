package com.example.moviechecker.model.episode

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @get:Query("SELECT * FROM episode")
    val episodes: Flow<List<Episode>>

    @get:Query("SELECT * FROM EpisodeDetail")
    val episodesDetailed: Flow<List<EpisodeDetail>>

    @Query("SELECT * FROM Episode")
    suspend fun loadAll(): List<Episode>

    @Query("SELECT * FROM EpisodeDetail e WHERE e.state = 'RELEASED' ORDER BY e.date DESC")
    fun loadReleased(): Flow<List<EpisodeDetail>>

    @Query("SELECT * FROM EpisodeDetail e WHERE e.state = 'EXPECTED' ORDER BY e.date ASC")
    fun loadExpected(): Flow<List<EpisodeDetail>>

    @Query("SELECT * FROM episode e WHERE e.season_id = :season_id AND e.number = :number")
    suspend fun loadBySeasonAndNumber(season_id: Int, number: Int): Episode?

    @Insert
    suspend fun insert(episode: Episode)

    @Update
    suspend fun update(episode: Episode)

    @Delete
    suspend fun delete(episode: Episode)

    @Query("DELETE FROM episode")
    suspend fun deleteAll()
}