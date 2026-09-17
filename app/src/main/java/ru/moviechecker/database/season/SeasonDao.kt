package ru.moviechecker.database.season

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons s WHERE s.movie_id = :movieId AND s.number = :number")
    suspend fun getSeasonByMovieIdAndNumber(movieId: Int, number: Int): SeasonEntity?

    @Query("SELECT * FROM seasons s WHERE s.movie_id = :movieId")
    suspend fun getSeasonsByMovieId(movieId: Int): List<SeasonEntity>

    @Transaction
    @Query("SELECT * FROM seasons s WHERE s.movie_id = :movieId")
    suspend fun getSeasonsWithEpisodesByMovieId(movieId: Int): List<SeasonWithEpisodes>

    @Query("SELECT COUNT(*) FROM seasons s WHERE s.movie_id = :movieId")
    fun getNumberOfSeasonsByMovieId(movieId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg seasons: SeasonEntity)
    @Update
    suspend fun update(vararg seasons: SeasonEntity)
    @Delete
    suspend fun delete(vararg season: SeasonEntity)
}