package ru.moviechecker.database.movies

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies m WHERE m.favorites_mark = :mark")
    fun getMoviesByFavoriteMark(mark: Boolean): List<MovieEntity>
    @Query("SELECT * FROM movies m WHERE m.id = :id")
    fun getMovieById(id: Int): MovieEntity?
    @Query("SELECT * FROM movies m")
    fun getMovies(): List<MovieEntity>
    @Query("SELECT count(*) FROM movies m")
    fun getCount(): Int
    @Query("SELECT * FROM movies m WHERE m.site_id = :siteId AND m.page_id = :pageId")
    fun getMovieBySiteIdAndPageId(siteId: Int, pageId: String): MovieEntity?

    @Query("SELECT * FROM v_movie_cards card")
    fun getMovieCardsStream(): Flow<List<MovieCardsView>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieByIdStream(id: Int): Flow<MovieEntity>

    @Transaction
    @Query("SELECT movie.id, " +
            "movie.site_id, " +
            "site.address, " +
            "movie.page_id, " +
            "movie.title, " +
            "CASE WHEN movie.link IS NOT NULL THEN movie.link ELSE season.link END AS link, " +
            "CASE WHEN movie.poster IS NOT NULL THEN movie.poster ELSE season.poster END AS poster, " +
            "movie.favorites_mark " +
            "FROM movies movie " +
            "JOIN sites site ON site.id = movie.site_id " +
            "JOIN seasons season ON season.movie_id = movie.id " +
            "WHERE movie.id = :id " +
            "ORDER BY season.number ASC " +
            "LIMIT 1")
    fun getMovieDetailsStream(id: Int): Flow<MovieDetails>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg movies: MovieEntity)
    @Update
    fun update(vararg movie: MovieEntity)
    @Delete
    fun delete(vararg movie: MovieEntity)
}