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
    fun loadAll(): List<Movie>

    @Query("SELECT * FROM Movie m WHERE m.id = :id")
    fun loadById(id: Int): Movie?

    @Query("SELECT * FROM movie m WHERE m.site_id = :site_id AND m.page_id = :pageId")
    fun loadMovieBySiteAndPageId(site_id: Int, pageId: String): Movie?

    @Query("SELECT * FROM movie m JOIN season s ON m.id = s.movie_id")
    fun loadMovieAndSeasons(): Map<Movie, List<Season>>

    @Insert
    fun insert(movie: Movie)

    @Update
    fun update(movie: Movie)

    @Delete
    fun delete(movie: Movie)

    @Query("DELETE FROM movie")
    fun deleteAll()
}