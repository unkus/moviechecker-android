package moviechecker.data.season

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @get:Query("SELECT * FROM season")
    val seasons: Flow<List<SeasonEntity>>

    @Query("SELECT * FROM season s WHERE s.movie_id = :movie_id and s.number = :number")
    fun findByMovieAndNumber(movie_id: Int, number: Int): SeasonEntity?

    @Insert
    fun insert(season: SeasonEntity)

    @Update
    fun update(season: SeasonEntity)

    @Delete
    fun delete(season: SeasonEntity)
}