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

    @Query("SELECT movie.id, " +
            "movie.page_id, " +
            "movie.link, " +
            "movie.title, " +
            "CASE WHEN movie.poster IS NOT NULL THEN movie.poster ELSE season.poster END AS 'poster', " +
            "movie.favorites_mark, " +
            "site.address site_address, " +
            "COUNT(season.id) season_count " +
            "FROM movies movie " +
            "JOIN sites site ON site.id = movie.site_id " +
            "JOIN seasons season ON season.movie_id = movie.id " +
            "WHERE movie.id = :id " +
            "GROUP BY movie.id")
    fun getMovieDetailsByIdStream(id: Int): Flow<MovieDetails>

    @Transaction
    @Query("SELECT movie.id, " +
            "movie.site_id, " +
            "movie.page_id, " +
            "movie.title, " +
            "movie.poster, " +
            "movie.link, " +
            "movie.favorites_mark, " +
            "site.id, " +
            "site.address, " +
            "season.id, " +
            "season.movie_id, " +
            "season.number, " +
            "season.title, " +
            "season.poster, " +
            "season.link, " +
            "episode.id, " +
            "episode.season_id, " +
            "episode.number, " +
            "episode.title, " +
            "episode.link, " +
            "episode.date, " +
            "episode.state " +
            "FROM movies movie " +
            "JOIN sites site ON site.id = movie.site_id " +
            "JOIN seasons season ON season.movie_id = movie.id " +
            "JOIN episodes episode ON episode.season_id = season.id " +
            "WHERE movie.id = :id")
    fun getMovieByIdWithSeasonsStream(id: Int): Flow<MovieWithSiteAndSeasons>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg movies: MovieEntity)
    @Update
    fun update(vararg movie: MovieEntity)
    @Delete
    fun delete(vararg movie: MovieEntity)
}