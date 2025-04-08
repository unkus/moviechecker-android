package ru.moviechecker.database.movies

import androidx.room.ColumnInfo
import java.net.URI
import java.time.LocalDateTime

data class MovieCard2(
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

        other as MovieCard2

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