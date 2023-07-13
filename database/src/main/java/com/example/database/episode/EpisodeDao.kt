package com.example.database.episode

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @get:Query("SELECT * FROM episode")
    val episodes: Flow<List<Episode>>

    @get:Query("SELECT * FROM EpisodeDetail")
    val episodesDetailed: Flow<List<EpisodeDetail>>

    @Query("SELECT * FROM Episode")
    fun loadAll(): List<Episode>

    @Query("SELECT * FROM EpisodeDetail e " +
            "WHERE e.state != 'EXPECTED' ORDER BY e.date DESC")
    fun loadReleased(): Flow<List<EpisodeDetail>>

    @Query("SELECT * FROM EpisodeDetail e " +
            "WHERE e.state = 'EXPECTED' ORDER BY e.date ASC")
    fun loadExpected(): Flow<List<EpisodeDetail>>

    @Query("SELECT e.* FROM episode e " +
            "JOIN movie m, season s, favorite f ON e.season_id = s.id AND s.movie_id = m.id AND m.id = f.movie_id " +
            "WHERE e.state = 'VIEWED' and e.id != f.last_viewed")
    fun findViewedEpisodesWithExclusion(): List<Episode>?

    @Query("SELECT * FROM episode e " +
            "WHERE e.season_id = :season_id AND e.number = :number")
    fun loadBySeasonAndNumber(season_id: Int, number: Int): Episode?

    @Query("SELECT e.* FROM episode e " +
            "JOIN site, movie m, season s ON e.season_id = s.id AND s.movie_id = m.id AND m.site_id = site.id " +
            "WHERE e.number = :number AND s.number = :seasonNumber AND m.page_id = :moviePageId AND site.link = :siteAddress")
    fun findBySiteAndMovieAndSeasonAndNumber(siteAddress: Uri, moviePageId: String, seasonNumber: Int, number: Int): Episode?

    @Insert
    fun insert(episode: Episode)

    @Update
    fun update(episode: Episode)

    @Delete
    fun delete(episode: Episode)

    @Query("DELETE FROM episode")
    fun deleteAll()
}