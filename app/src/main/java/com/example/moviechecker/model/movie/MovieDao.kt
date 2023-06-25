package com.example.moviechecker.model.movie

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moviechecker.model.season.Season
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @get:Query("SELECT * FROM movie")
    val movies: Flow<List<Movie>>

    @Query("SELECT * FROM Movie")
    suspend fun loadAll(): List<Movie>

    @Query("SELECT * FROM Movie m WHERE m.id = :id")
    suspend fun loadById(id: Int): Movie?

    @Query("SELECT * FROM movie m WHERE m.site_id = :site_id AND m.page_id = :pageId")
    suspend fun loadMovieBySiteAndPageId(site_id: Int, pageId: String): Movie?

    @Query("SELECT * FROM movie m JOIN season s ON m.id = s.movie_id")
    suspend fun loadMovieAndSeasons(): Map<Movie, List<Season>>


    @Insert
    suspend fun insert(movie: Movie)

    @Update
    suspend fun update(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)

    @Query("DELETE FROM movie")
    suspend fun deleteAll()
}