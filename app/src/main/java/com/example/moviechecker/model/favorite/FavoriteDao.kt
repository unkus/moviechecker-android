package com.example.moviechecker.model.favorite

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moviechecker.model.movie.Movie
import com.example.moviechecker.model.site.Site
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @get:Query("SELECT * FROM favorite")
    val favorites: Flow<List<Favorite>>

    @get:Query("SELECT * FROM FavoriteDetail")
    val favoritesDetailed: Flow<List<FavoriteDetail>>

    @Query("SELECT * FROM Favorite")
    suspend fun loadAll(): List<Favorite>

    @Query("SELECT * FROM favorite f WHERE f.movie_id = :movie_id")
    suspend fun findByMovie(movie_id: Int): Favorite?

    // SELECT * FROM movie m JOIN season s ON m.id = s.movie_id
    @Query("SELECT f.* FROM favorite f JOIN movie m, site s ON m.id = f.movie_id AND m.site_id = s.id AND s.link = :siteAddress AND m.page_id = :moviePageId")
    suspend fun loadBySiteAndMovie(siteAddress: Uri?, moviePageId: String?): Favorite?

    @Query("SELECT m.* FROM movie m JOIN site s ON m.site_id = s.id AND m.page_id = :moviePageId AND s.link = :siteAddress")
    suspend fun loadMovieBySiteAndPage(siteAddress: Uri?, moviePageId: String?): Movie

    //    @Query("SELECT CASE WHEN count(f)> 0 THEN true ELSE false END FROM favorite f WHERE f.movie = :movie ")
    //    boolean existsByMovie(Movie movie);
    //
    //    @Query("SELECT CASE WHEN count(f)> 0 THEN true ELSE false END FROM favorite f WHERE f.lastViewed = :episode ")
    //    boolean existsByLastViewed(Episode episode);
    @Insert
    suspend fun insert(favorite: Favorite)

    @Update
    suspend fun update(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("DELETE FROM favorite")
    suspend fun deleteAll()

}