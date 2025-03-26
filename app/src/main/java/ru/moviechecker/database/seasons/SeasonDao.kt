package ru.moviechecker.database.seasons

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons s WHERE s.movie_id = :movieId AND s.number = :number")
    fun getSeasonByMovieIdAndNumber(movieId: Int, number: Int): SeasonEntity?

    @Query("SELECT * FROM seasons s WHERE s.movie_id = :movieId")
    fun getSeasonsByMovieIdStream(movieId: Int): Flow<List<SeasonEntity>>

    @Transaction
    @Query("SELECT * FROM seasons s WHERE s.movie_id = :movieId")
    fun getSeasonsWithEpisodesByMovieIdStream(movieId: Int): Flow<List<SeasonWithEpisodes>>

    @Query("SELECT COUNT(*) FROM seasons s WHERE s.movie_id = :movieId")
    fun getNumberOfSeasonsByMovieId(movieId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg seasons: SeasonEntity)
    @Update
    fun update(vararg seasons: SeasonEntity)
    @Delete
    fun delete(vararg season: SeasonEntity)
}