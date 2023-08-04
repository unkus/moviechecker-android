package moviechecker.data.favorite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import moviechecker.data.movie.MovieEntity
import kotlinx.coroutines.flow.Flow
import java.net.URI

@Dao
interface FavoriteDao {
    @get:Query("SELECT * FROM favorite")
    val favorites: Flow<List<FavoriteEntity>>

    @get:Query("SELECT * FROM FavoriteDetail")
    val favoritesDetailed: Flow<List<FavoriteDetail>>

    @Query("SELECT * FROM FavoriteDetail")
    fun loadAll(): Flow<List<FavoriteDetail>>

    @Query("SELECT * FROM favorite f WHERE f.movie_id = :movie_id")
    fun findByMovie(movie_id: Int): FavoriteEntity?

    // SELECT * FROM movie m JOIN season s ON m.id = s.movie_id
    @Query("SELECT f.* FROM favorite f JOIN movie m, site s ON m.id = f.movie_id AND m.site_id = s.id AND s.link = :siteAddress AND m.page_id = :moviePageId")
    fun loadBySiteAndMovie(siteAddress: URI?, moviePageId: String?): FavoriteEntity?

    @Query("SELECT m.* FROM movie m JOIN site s ON m.site_id = s.id AND m.page_id = :moviePageId AND s.link = :siteAddress")
    fun loadMovieBySiteAndPage(siteAddress: URI?, moviePageId: String?): MovieEntity

    //    @Query("SELECT CASE WHEN count(f)> 0 THEN true ELSE false END FROM favorite f WHERE f.movie = :movie ")
    //    boolean existsByMovie(Movie movie);
    //
    //    @Query("SELECT CASE WHEN count(f)> 0 THEN true ELSE false END FROM favorite f WHERE f.lastViewed = :episode ")
    //    boolean existsByLastViewed(Episode episode);

    @Insert
    fun insert(favorite: FavoriteEntity)

    @Update
    fun update(favorite: FavoriteEntity)

    @Delete
    fun delete(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite")
    fun deleteAll()

}