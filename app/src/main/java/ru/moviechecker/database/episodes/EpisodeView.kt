package ru.moviechecker.database.episodes

import androidx.room.DatabaseView
import java.net.URI
import java.time.LocalDateTime

@DatabaseView(
    viewName = "v_episodes",
    value = "SELECT sites.id as 'siteId', sites.address as 'siteAddress', " +
            "m.id as 'movieId', m.page_id as 'moviePageId', m.title as 'movieTitle', m.link as 'movieLink', m.poster as 'moviePoster', m.favorites_mark as 'movieFavoritesMark', " +
            "s.id as 'seasonId', s.title as 'seasonTitle', s.number as 'seasonNumber', s.link as 'seasonLink', s.poster as 'seasonPoster', " +
            "e.id as 'episodeId', e.number as 'episodeNumber', e.title as 'episodeTitle', e.link as 'episodeLink', e.state as 'episodeState', e.date as 'episodeDate' " +
            "FROM episodes e, seasons s, movies m, sites " +
            "WHERE e.season_id = s.id AND s.movie_id = m.id AND m.site_id = sites.id " +
            "ORDER BY e.date DESC"
)
data class EpisodeView(
    override val siteId: Int,
    override val siteAddress: URI,

    override val movieId: Int,
    override val moviePageId: String,
    override val movieTitle: String,
    override val movieLink: URI? = null,
    override val moviePoster: ByteArray? = null,
    override val movieFavoritesMark: Boolean = false,

    override val seasonId: Int,
    override val seasonNumber: Int,
    override val seasonTitle: String?,
    override val seasonLink: URI? = null,
    override val seasonPoster: ByteArray? = null,

    override val episodeId: Int,
    override val episodeNumber: Int,
    override val episodeTitle: String?,
    override val episodeLink: URI,
    override val episodeState: EpisodeState,
    override val episodeDate: LocalDateTime
): IEpisodeView {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EpisodeView

        if (siteAddress != other.siteAddress) return false
        if (moviePageId != other.moviePageId) return false
        if (movieTitle != other.movieTitle) return false
        if (movieFavoritesMark != other.movieFavoritesMark) return false
        if (seasonNumber != other.seasonNumber) return false
        if (seasonTitle != other.seasonTitle) return false
        if (episodeNumber != other.episodeNumber) return false
        if (episodeTitle != other.episodeTitle) return false
        if (episodeState != other.episodeState) return false
        if (episodeDate != other.episodeDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = siteAddress.hashCode()
        result = 31 * result + moviePageId.hashCode()
        result = 31 * result + movieTitle.hashCode()
        result = 31 * result + movieFavoritesMark.hashCode()
        result = 31 * result + seasonNumber
        result = 31 * result + (seasonTitle?.hashCode() ?: 0)
        result = 31 * result + episodeNumber
        result = 31 * result + (episodeTitle?.hashCode() ?: 0)
        result = 31 * result + episodeState.hashCode()
        result = 31 * result + episodeDate.hashCode()
        return result
    }


}
