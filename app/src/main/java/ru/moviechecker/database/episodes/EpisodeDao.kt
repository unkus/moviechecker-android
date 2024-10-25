package ru.moviechecker.database.episodes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes e ORDER BY e.date DESC")
    fun getAllEpisodesStream(): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes e WHERE e.id = :id")
    fun getEpisodeById(id: Int): Flow<EpisodeEntity>

    @Query("SELECT * FROM v_episodes v ORDER BY v.episodeDate DESC")
    fun getAllEpisodesViewStream(): Flow<List<EpisodeView>>

    @Query("SELECT * FROM v_episodes v WHERE v.episodeState = 'EXPECTED' ORDER BY v.episodeDate ASC")
    fun getExpectedEpisodesViewStream(): Flow<List<EpisodeView>>

    @Query("SELECT * FROM v_episodes v WHERE v.episodeState = 'RELEASED' OR v.episodeState = 'VIEWED' ORDER BY v.episodeDate DESC")
    fun getReleasedEpisodesViewStream(): Flow<List<EpisodeView>>

    @Query("SELECT * FROM episodes e WHERE e.id = :id")
    fun getById(id: Int): EpisodeEntity?
    @Query("SELECT * FROM episodes e WHERE e.season_id = :seasonId AND e.number = :number")
    fun getBySeasonIdAndNumber(seasonId: Int, number: Int): EpisodeEntity?
    @Query("SELECT * FROM episodes e WHERE e.state = :state ORDER BY e.season_id, e.number ASC")
    fun getByStateSortByNumberAsc(state: EpisodeState): List<EpisodeEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg episodes: EpisodeEntity)
    @Update
    fun update(vararg episodes: EpisodeEntity)
    @Delete
    fun delete(vararg episodes: EpisodeEntity)
}