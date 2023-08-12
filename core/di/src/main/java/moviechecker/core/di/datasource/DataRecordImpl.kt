package moviechecker.core.di.datasource

import moviechecker.core.di.State
import java.net.URI
import java.time.LocalDateTime

data class DataRecordImpl(
    override val siteAddress: URI,
    override val moviePageId: String,
    override val movieTitle: String,
    override val movieLink: URI,
    override val posterLink: URI?,
    override val seasonNumber: Int,
    override val seasonLink: URI,
    override val episodeNumber: Int,
    override val episodeTitle: String,
    override val episodeLink: URI,
    override val episodeState: State,
    override val episodeDate: LocalDateTime
) : DataRecord {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataRecordImpl

        if (siteAddress != other.siteAddress) return false
        if (moviePageId != other.moviePageId) return false
        if (seasonNumber != other.seasonNumber) return false
        if (episodeNumber != other.episodeNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = siteAddress.hashCode()
        result = 31 * result + moviePageId.hashCode()
        result = 31 * result + seasonNumber
        result = 31 * result + episodeNumber
        return result
    }
}