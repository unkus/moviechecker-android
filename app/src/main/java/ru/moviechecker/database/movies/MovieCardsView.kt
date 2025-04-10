package ru.moviechecker.database.movies

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import java.net.URI
import java.time.LocalDateTime

@DatabaseView(
    viewName = "v_movie_cards",
    value = "SELECT movie.id, " +
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
            "GROUP BY season.id " +
            "ORDER BY last_episode_date DESC"
)
data class MovieCardsView(
    val id: Int,
    @ColumnInfo("season_number")
    val seasonNumber: Int,
    val title: String,
    val poster: ByteArray? = null,
    @ColumnInfo("favorites_mark")
    val favoritesMark: Boolean,
    @ColumnInfo("next_episode_id")
    val nextEpisodeId: Int? = null,
    @ColumnInfo("next_episode_number")
    val nextEpisodeNumber: Int? = null,
    @ColumnInfo("next_episode_title")
    val nextEpisodeTitle: String? = null,
    @ColumnInfo("next_episode_link")
    val nextEpisodeLink: URI? = null,
    @ColumnInfo("next_episode_date")
    val nextEpisodeDate: LocalDateTime? = null,
    @ColumnInfo("last_episode_id")
    val lastEpisodeId: Int,
    @ColumnInfo("last_episode_number")
    val lastEpisodeNumber: Int,
    @ColumnInfo("last_episode_title")
    val lastEpisodeTitle: String?,
    @ColumnInfo("last_episode_link")
    val lastEpisodeLink: URI,
    @ColumnInfo("last_episode_date")
    val lastEpisodeDate: LocalDateTime,
    @ColumnInfo("viewed_mark")
    val viewedMark: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieCardsView

        if (id != other.id) return false
        if (seasonNumber != other.seasonNumber) return false
        if (title != other.title) return false
        if (poster != null) {
            if (other.poster == null) return false
            if (!poster.contentEquals(other.poster)) return false
        } else if (other.poster != null) return false
        if (favoritesMark != other.favoritesMark) return false
        if (nextEpisodeId != other.nextEpisodeId) return false
        if (nextEpisodeNumber != other.nextEpisodeNumber) return false
        if (nextEpisodeTitle != other.nextEpisodeTitle) return false
        if (nextEpisodeLink != other.nextEpisodeLink) return false
        if (nextEpisodeDate != other.nextEpisodeDate) return false
        if (lastEpisodeId != other.lastEpisodeId) return false
        if (lastEpisodeNumber != other.lastEpisodeNumber) return false
        if (lastEpisodeTitle != other.lastEpisodeTitle) return false
        if (lastEpisodeLink != other.lastEpisodeLink) return false
        if (lastEpisodeDate != other.lastEpisodeDate) return false
        if (viewedMark != other.viewedMark) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + seasonNumber
        result = 31 * result + title.hashCode()
        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + favoritesMark.hashCode()
        result = 31 * result + (nextEpisodeId ?: 0)
        result = 31 * result + (nextEpisodeNumber ?: 0)
        result = 31 * result + (nextEpisodeTitle?.hashCode() ?: 0)
        result = 31 * result + (nextEpisodeLink?.hashCode() ?: 0)
        result = 31 * result + (nextEpisodeDate?.hashCode() ?: 0)
        result = 31 * result + (lastEpisodeId)
        result = 31 * result + (lastEpisodeNumber)
        result = 31 * result + (lastEpisodeTitle?.hashCode() ?: 0)
        result = 31 * result + (lastEpisodeLink.hashCode())
        result = 31 * result + (lastEpisodeDate.hashCode())
        result = 31 * result + viewedMark.hashCode()
        return result
    }

}