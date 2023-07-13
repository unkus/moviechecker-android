package com.example.database.site

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.database.movie.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {
    @get:Query("SELECT * FROM site")
    val sites: Flow<List<Site>>

    @Query("SELECT * FROM site")
    fun loadAll(): List<Site>

    @Query("SELECT * FROM site s WHERE s.link = :address")
    fun loadSiteByAddress(address: Uri): Site?

    @Query("SELECT * FROM site s JOIN movie m ON s.id = m.site_id")
    fun loadSiteAndMovies(): Map<Site, List<Movie>>

    @Insert
    fun insert(site: Site)

    @Update
    fun update(site: Site)

    @Delete
    fun delete(site: Site)

    @Query("DELETE FROM site")
    fun deleteAll()
}