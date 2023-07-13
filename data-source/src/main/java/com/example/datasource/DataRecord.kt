package com.example.datasource

import android.net.Uri
import com.example.database.api.State
import java.time.LocalDateTime

data class DataRecord(
    val siteAddress: Uri,
    val moviePageId: String,
    val movieTitle: String,
    val movieLink: Uri,
    val posterLink: Uri?,
    val seasonNumber: Int,
    val seasonLink: Uri,
    val episodeNumber: Int,
    val episodeTitle: String,
    val episodeLink: Uri,
    val episodeState: State,
    val episodeDate: LocalDateTime
) {

    class Builder {
        private var siteAddress: Uri? = null
        private var moviePageId: String? = null
        private var movieTitle: String? = null
        private var movieLink: Uri? = null
        private var posterLink: Uri? = null
        private var seasonNumber: Int? = null
        private var seasonLink: Uri? = null
        private var episodeNumber: Int? = null
        private var episodeTitle: String? = null
        private var episodeLink: Uri? = null
        private var episodeState: State? = null
        private var episodeDate: LocalDateTime? = null

        fun site(site: Uri): Builder {
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

        fun movieLink(movieLink: Uri): Builder {
            this.movieLink = movieLink
            return this
        }

        fun posterLink(posterLink: Uri): Builder {
            this.posterLink = posterLink
            return this
        }

        fun seasonNumber(seasonNumber: Int): Builder {
            this.seasonNumber = seasonNumber
            return this
        }

        fun seasonLink(seasonLink: Uri): Builder {
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

        fun episodeLink(episodeLink: Uri): Builder {
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
            return DataRecord(
                siteAddress!!,
                moviePageId!!,
                movieTitle!!,
                movieLink!!,
                posterLink,
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