package moviechecker.data.movie

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.net.URI

@Dao
interface MovieDao {
    @get:Query("SELECT * FROM movie")
    val movies: Flow<List<MovieEntity>>

    @Query(
        "SELECT m.* FROM movie m " +
                "LEFT JOIN favorite f ON f.movie_id = m.id " +
                "WHERE f.id IS NULL "
    )
    fun findNotInFavorites(): List<MovieEntity>

    @Query(
        "SELECT * FROM movie m " +
                "WHERE m.site_id = :site_id AND m.page_id = :pageId"
    )
    fun loadMovieBySiteAndPageId(site_id: Int, pageId: String): MovieEntity?

    @Query(
        "SELECT m.* FROM movie m " +
                "JOIN site s ON s.id = m.site_id " +
                "WHERE s.link = :siteAddress AND m.page_id = :pageId"
    )
    fun loadMovieBySiteAddressAndPageId(siteAddress: URI, pageId: String): MovieEntity?

    @Insert
    fun insert(movie: MovieEntity)

    @Update
    fun update(movie: MovieEntity)

    @Delete
    fun delete(vararg movies: MovieEntity)

}