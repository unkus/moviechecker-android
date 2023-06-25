package com.example.moviechecker.model.site

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moviechecker.model.movie.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {
    @get:Query("SELECT * FROM site")
    val sites: Flow<List<Site>>

    @Query("SELECT * FROM Site")
    suspend fun loadAll(): List<Site>

    @Query("SELECT * FROM site s WHERE s.link = :address")
    suspend fun loadSiteByAddress(address: Uri): Site?

    @Query("SELECT * FROM site s JOIN movie m ON s.id = m.site_id")
    suspend fun loadSiteAndMovies(): Map<Site, List<Movie>>

    @Insert
    suspend fun insert(site: Site)

    @Update
    suspend fun update(site: Site)

    @Delete
    suspend fun delete(site: Site)

    @Query("DELETE FROM site")
    suspend fun deleteAll()
}