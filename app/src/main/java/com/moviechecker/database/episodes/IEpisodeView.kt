package com.moviechecker.database.episodes

import java.net.URI
import java.time.LocalDateTime

interface IEpisodeView {
    val siteId: Int
    val siteAddress: URI

    val movieId: Int
    val moviePageId: String
    val movieTitle: String
    val movieLink: URI?
    val moviePoster: ByteArray?
    val movieFavoritesMark: Boolean

    val seasonId: Int
    val seasonNumber: Int
    val seasonTitle: String?
    val seasonLink: URI?
    val seasonPoster: ByteArray?

    val episodeId: Int
    val episodeNumber: Int
    val episodeTitle: String?
    val episodeLink: URI
    val episodeState: EpisodeState
    val episodeDate: LocalDateTime
}