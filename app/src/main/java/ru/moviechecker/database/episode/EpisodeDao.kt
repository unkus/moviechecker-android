package ru.moviechecker.database.episode

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes e ORDER BY e.date DESC")
    fun getAllEpisodesStream(): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes e WHERE e.id = :id")
    fun getEpisodeById(id: Int): Flow<EpisodeEntity>

    @Query("SELECT * FROM episodes e WHERE e.season_id = :seasonId")
    fun getEpisodesBySeasonId(seasonId: Int): Flow<List<EpisodeEntity>>
    @Query("SELECT * FROM episodes e WHERE e.season_id in (:seasonId)")
    fun getEpisodesBySeasonId(seasonId: List<Int>): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes e WHERE e.id = :id")
    suspend fun getById(id: Int): EpisodeEntity
    @Query("SELECT * FROM episodes e WHERE e.season_id = :seasonId ORDER BY e.number DESC LIMIT 1")
    suspend fun getLastBySeasonId(seasonId: Int): EpisodeEntity?
    @Query("SELECT * FROM episodes e WHERE e.state = :state ORDER BY e.season_id, e.number ASC")
    suspend fun getByStateSortByNumberAsc(state: EpisodeState): List<EpisodeEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg episodes: EpisodeEntity)
    @Update
    suspend fun update(vararg episodes: EpisodeEntity)
    @Delete
    suspend fun delete(vararg episodes: EpisodeEntity)

    @Query("UPDATE episodes SET state = :newState WHERE id = :episodeId")
    suspend fun updateEpisodeState(episodeId: Int, newState: EpisodeState)
}