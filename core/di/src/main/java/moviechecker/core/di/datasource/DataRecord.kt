package moviechecker.core.di.datasource

import moviechecker.core.di.State
import java.net.URI
import java.time.LocalDateTime

interface DataRecord {
    val siteAddress: URI
    val moviePageId: String
    val movieTitle: String
    val movieLink: URI
    val posterLink: URI?
    val seasonNumber: Int
    val seasonLink: URI
    val episodeNumber: Int
    val episodeTitle: String
    val episodeLink: URI
    val episodeState: State
    val episodeDate: LocalDateTime

    class Builder {
        private var siteAddress: URI? = null
        private var moviePageId: String? = null
        private var movieTitle: String? = null
        private var movieLink: URI? = null
        private var moviePosterLink: URI? = null
        private var seasonNumber: Int? = null
        private var seasonLink: URI? = null
        private var episodeNumber: Int? = null
        private var episodeTitle: String? = null
        private var episodeLink: URI? = null
        private var episodeState: State? = null
        private var episodeDate: LocalDateTime? = null

        fun site(site: URI): Builder {
            siteAddress = site
            return this
        }

        fun moviePageId(moviePageId: String): Builder {
            this.moviePageId = moviePageId
            return this
        }

        fun movieTitle(movieTitle: String): Builder {
            this.movieTitle = movieTitle
            return this
        }

        fun movieLink(movieLink: URI): Builder {
            this.movieLink = movieLink
            return this
        }

        fun moviePosterLink(moviePosterLink: URI): Builder {
            this.moviePosterLink = moviePosterLink
            return this
        }

        fun seasonNumber(seasonNumber: Int): Builder {
            this.seasonNumber = seasonNumber
            return this
        }

        fun seasonLink(seasonLink: URI): Builder {
            this.seasonLink = seasonLink
            return this
        }

        fun episodeNumber(episodeNumber: Int): Builder {
            this.episodeNumber = episodeNumber
            return this
        }

        fun episodeTitle(episodeTitle: String): Builder {
            this.episodeTitle = episodeTitle
            return this
        }

        fun episodeLink(episodeLink: URI): Builder {
            this.episodeLink = episodeLink
            return this
        }

        fun episodeState(episodeState: State): Builder {
            this.episodeState = episodeState
            return this
        }

        fun episodeDate(episodeDate: LocalDateTime): Builder {
            this.episodeDate = episodeDate
            return this
        }

        fun build(): DataRecord {
            return DataRecordImpl(
                siteAddress!!,
                moviePageId!!,
                movieTitle!!,
                movieLink!!,
                moviePosterLink,
                seasonNumber!!,
                seasonLink!!,
                episodeNumber!!,
                episodeTitle!!,
                episodeLink!!,
                episodeState!!,
                episodeDate!!
            )
        }
    }
}