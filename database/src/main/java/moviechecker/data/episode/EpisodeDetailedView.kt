package moviechecker.data.episode

import androidx.room.DatabaseView
import moviechecker.core.di.database.episode.Episode
import moviechecker.core.di.State
import java.net.URI
import java.time.LocalDateTime

@DatabaseView("SELECT site.link as siteAddress, " +
        "movie.page_id as moviePageId, " +
        "movie.title as movieTitle, " +
        "movie.poster_link as posterLink, " +
        "season.number as seasonNumber, " +
        "e.number, " +
        "e.title, " +
        "(site.link || TRIM(season.link, \".html\") || e.link) as link, " +
        "e.state, " +
        "e.date, " +
//        "CAST((SELECT id FROM favorite WHERE movie_id = movie.id) AS BOOLEAN) as isInFavorite " +
        "EXISTS(SELECT id FROM favorite WHERE movie_id = movie.id) as isInFavorite " +
        "FROM episode e " +
        "INNER JOIN site, movie, season " +
        "ON site.id = movie.site_id " +
        "AND movie.id = season.movie_id " +
        "AND season.id = e.season_id")
data class EpisodeDetailedView(
    override val siteAddress: URI,
    override val moviePageId: String,
    override val movieTitle: String,
    override val posterLink: URI?,
    override val seasonNumber: Int,
    override val number: Int,
    override val title: String,
    override val link: URI,
    override val state: State,
    override val date: LocalDateTime,
    override val isInFavorite: Boolean
): Episode
