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

    @Query("SELECT movie.id, " +
            "season.id 'season_id', season.number 'season_number'," +
            "CASE WHEN season.title IS NOT NULL THEN season.title ELSE movie.title || ' (' || season.number || ')' END AS 'title', " +
            "CASE WHEN season.poster IS NOT NULL THEN season.poster ELSE movie.poster END AS 'poster', " +
            "movie.favorites_mark, " +
            "next_episode.id 'next_episode_id', " +
            "next_episode.number 'next_episode_number', " +
            "next_episode.title 'next_episode_title', " +
            "site.address || next_episode.link 'next_episode_link', " +
            "next_episode.date 'next_episode_date', " +
            "last_episode.id 'last_episode_id', " +
            "last_episode.number 'last_episode_number', " +
            "last_episode.title 'last_episode_title', " +
            "site.address || last_episode.link 'last_episode_link', " +
            "last_episode.date 'last_episode_date', " +
            "CASE WHEN last_episode.state = 'VIEWED' THEN true ELSE false END AS 'viewed_mark' " +
            "FROM movies movie " +
            "JOIN sites site ON site.id = movie.site_id " +
            "JOIN seasons season ON season.movie_id = movie.id " +
            "LEFT JOIN (SELECT e.id, e.season_id, e.number, e.date, e.title, e.link, MIN(e.date) 'date' FROM episodes e WHERE e.state = 'RELEASED' GROUP BY e.season_id) 'next_episode' ON next_episode.season_id = season.id " +
            "JOIN (SELECT e.id, e.season_id, e.number, e.date, e.title, e.link, e.state, MAX(e.date) 'date' FROM episodes e WHERE e.state IN ('RELEASED', 'VIEWED') GROUP BY e.season_id) 'last_episode' ON last_episode.season_id = season.id " +
            "WHERE :siteId IS NULL OR movie.site_id = :siteId " +
            "GROUP BY season.id " +
            "ORDER BY last_episode_date DESC")
    fun getMovieCardsStream(siteId: Int?): Flow<List<MovieCard2>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieByIdStream(id: Int): Flow<MovieEntity>

    @Transaction
    @Query(
        "SELECT movie.id, " +
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
                "LIMIT 1"
    )
    fun getMovieDetailsStream(id: Int): Flow<MovieDetails>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg movies: MovieEntity)

    @Update
    fun update(vararg movie: MovieEntity)

    @Delete
    fun delete(vararg movie: MovieEntity)
}