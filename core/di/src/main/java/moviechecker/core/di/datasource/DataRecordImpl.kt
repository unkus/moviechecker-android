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


}