package ru.moviechecker.database.seasons

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons s WHERE s.movie_id = :movieId AND s.number = :number")
    suspend fun getSeasonByMovieIdAndNumber(movieId: Int, number: Int): SeasonEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg seasons: SeasonEntity)
    @Update
    suspend fun update(vararg seasons: SeasonEntity)
    @Delete
    suspend fun delete(vararg season: SeasonEntity)
}