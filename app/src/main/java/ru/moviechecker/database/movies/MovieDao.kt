package ru.moviechecker.database.movies

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.seasons.SeasonEntity
import ru.moviechecker.database.sites.SiteEntity

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies m WHERE m.favorites_mark = :mark")
    fun getMoviesByFavoriteMark(mark: Boolean): List<MovieEntity>

    @Query("SELECT * FROM movies m WHERE m.id = :id")
    fun getMovieById(id: Int): MovieEntity

    @Query("SELECT * FROM movies m")
    fun getMovies(): List<MovieEntity>

    @Query("SELECT count(*) FROM movies m")
    fun getCount(): Int

    @Query("SELECT * FROM movies m WHERE m.site_id = :siteId AND m.page_id = :pageId")
    fun getMovieBySiteIdAndPageId(siteId: Int, pageId: String): MovieEntity?

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieByIdStream(id: Int): Flow<MovieEntity>

    @Query(
        "SELECT * FROM sites site " +
                "JOIN movies movie ON movie.site_id = site.id " +
                "JOIN seasons season ON season.movie_id = movie.id " +
                "JOIN episodes episode ON episode.season_id = season.id " +
                "WHERE movie.id = :id"
    )
    fun getMovieDetails(id: Int): Map<SiteEntity, Map<MovieEntity, Map<SeasonEntity, List<EpisodeEntity>>>>

    @Query(
        "WITH last_episodes AS ( " +
                "SELECT episode.season_id, MAX(episode.number) as number, episode.date " +
                "FROM episodes episode " +
                "WHERE episode.state != 'EXPECTED' " +
                "GROUP BY episode.season_id" +
                "), " +
                "last_seasons AS (" +
                "SELECT season.movie_id, MAX(season.number) as number " +
                "FROM seasons season " +
                "JOIN last_episodes last_episode ON last_episode.season_id = season.id " +
                "GROUP BY season.movie_id" +
                ") " +
                "SELECT " +
                "movie.id as id, " +
                "COALESCE(season.title, movie.title) as title, " +
                "COALESCE(season.poster, movie.poster) as poster, " +
                "movie.favorites_mark as favorites_mark, " +

                "site.id as site_id, " + // для фильтра по сайту
                "site.address as site_address, " + // для формирования ссылки
                "site.use_mirror as site_use_mirror, " + // для формирования ссылки
                "site.mirror as site_mirror, " + // для формирования ссылки
                "movie.id as movie_id, " + // для добавления/удаления в/из избранного
                "movie.title as movie_title, " + // для отображения если нету названия у сезона
                "movie.poster as movie_poster, " + // для отобрадения если нету постера у сезона
                "movie.favorites_mark as movie_favorites_mark, " + // для отображения и фильтра
                "last_season.number as movie_last_season_number, " + // для отображения последний/не последний
                // первый не просмотренный или последний просмотренный сезон
                "season.id as season_id, " + // возможно для уникального ключа в списке, но это не точно
                "COALESCE(MIN(CASE WHEN episode.state = 'RELEASED' THEN season.number END), season.number) as season_number, " + // для отображения если нет названия
                "season.title as season_title, " + // для отображения
                "season.poster as season_poster, " + // для отображения
                "season.link as season_link, " + // для формирования ссылки (!!! пока не используется)
                "last_episode.number as season_last_episode_number, " + // для отображения последний/не последний
                "last_episode.date as season_last_episode_date, " + // для сортировки
                // первый не просмотренный или последний просмотренный эпизод
                "episode.id as episode_id, " + // для проставления статуса
                "COALESCE(MIN(CASE WHEN episode.state = 'RELEASED' THEN episode.number END), episode.number) as episode_number, " + // для отображения
                "episode.title as episode_title, " + // для отображения
                "(episode.state = 'VIEWED') as episode_viewed_mark, " + // для отображения и фильтра
                "episode.date as episode_date, " + // для отображения
                "episode.link as episode_link " + // для перехода в браузер
                "FROM sites site " +
                "JOIN movies movie ON movie.site_id = site.id " +
                "JOIN seasons season ON season.movie_id = movie.id " +
                "JOIN episodes episode ON episode.season_id = season.id " +
                "JOIN last_seasons last_season ON last_season.movie_id = movie.id " +
                "JOIN last_episodes last_episode ON last_episode.season_id = season.id " +
                "WHERE episode.state != 'EXPECTED' " +
                "GROUP BY movie.id " +
                "ORDER BY last_episode.date DESC"
    )
    fun getMovieCardStream(): Flow<List<MovieCard>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg movies: MovieEntity)

    @Update
    fun update(vararg movie: MovieEntity)

    @Delete
    fun delete(vararg movie: MovieEntity)
}