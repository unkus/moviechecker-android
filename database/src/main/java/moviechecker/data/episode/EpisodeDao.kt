package moviechecker.data.episode

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.net.URI

@Dao
interface EpisodeDao {
    @get:Query("SELECT * FROM episode")
    val episodes: Flow<List<EpisodeEntity>>

    @get:Query("SELECT * FROM EpisodeDetailedView")
    val episodesDetailed: Flow<List<EpisodeDetailedView>>

    @get:Query(
        "SELECT * FROM EpisodeDetailedView e " +
            "WHERE e.state != 'EXPECTED' ORDER BY e.date DESC")
    val released: Flow<List<EpisodeDetailedView>>

    @get:Query(
        "SELECT * FROM EpisodeDetailedView e " +
            "WHERE e.state = 'EXPECTED' ORDER BY e.date ASC")
    val expected: Flow<List<EpisodeDetailedView>>

    @Query("SELECT e.* FROM episode e " +
            "JOIN movie m, season s, favorite f ON e.season_id = s.id AND s.movie_id = m.id AND m.id = f.movie_id " +
            "WHERE e.state = 'VIEWED' and e.id != f.last_viewed")
    fun findViewedEpisodesWithExclusion(): List<EpisodeEntity>?

    @Query("SELECT * FROM episode e " +
            "WHERE e.season_id = :season_id AND e.number = :number")
    fun loadBySeasonAndNumber(season_id: Int, number: Int): EpisodeEntity?

    @Query("SELECT e.* FROM episode e " +
            "JOIN site, movie m, season s ON e.season_id = s.id AND s.movie_id = m.id AND m.site_id = site.id " +
            "WHERE e.number = :number AND s.number = :seasonNumber AND m.page_id = :moviePageId AND site.link = :siteAddress")
    fun findBySiteAndMovieAndSeasonAndNumber(siteAddress: URI, moviePageId: String, seasonNumber: Int, number: Int): EpisodeEntity?

    @Insert
    fun insert(episode: EpisodeEntity)

    @Update
    fun update(episode: EpisodeEntity)

    @Delete
    fun delete(episode: EpisodeEntity)

}