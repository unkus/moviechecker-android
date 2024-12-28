package ru.moviechecker.database.episodes

import java.net.URI
import java.time.LocalDateTime

interface IEpisodeView {
    val siteId: Int
    val siteAddress: URI

    val movieId: Int
    val moviePageId: String
    val movieTitle: String
    val movieLink: String?
    val moviePoster: ByteArray?
    val movieFavoritesMark: Boolean

    val seasonId: Int
    val seasonNumber: Int
    val seasonTitle: String?
    val seasonLink: String?
    val seasonPoster: ByteArray?

    val episodeId: Int
    val episodeNumber: Int
    val episodeTitle: String?
    val episodeLink: String
    val episodeState: EpisodeState
    val episodeDate: LocalDateTime
}