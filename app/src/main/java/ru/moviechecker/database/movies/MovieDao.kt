package ru.moviechecker.database.movies

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies m WHERE m.favorites_mark = :mark")
    suspend fun getMoviesByFavoriteMark(mark: Boolean): List<MovieEntity>
    @Query("SELECT * FROM movies m WHERE m.id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?
    @Query("SELECT * FROM movies m")
    suspend fun getMovies(): List<MovieEntity>
    @Query("SELECT count(*) FROM movies m")
    suspend fun getCount(): Int
    @Query("SELECT * FROM movies m WHERE m.site_id = :siteId AND m.page_id = :pageId")
    suspend fun getMovieBySiteIdAndPageId(siteId: Int, pageId: String): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg movies: MovieEntity)
    @Update
    suspend fun update(vararg movie: MovieEntity)
    @Delete
    suspend fun delete(vararg movie: MovieEntity)
}