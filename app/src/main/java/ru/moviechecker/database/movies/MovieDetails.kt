package ru.moviechecker.database.movies

import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.seasons.SeasonEntity

data class MovieDetails(
    val id: Int,
    val siteId: Int,
    val address: String,
    val pageId: String,
    val title: String,
    val link: String?,
    val poster: ByteArray?,
    val favoritesMark: Boolean,
    val seasons: Map<SeasonEntity, List<EpisodeEntity>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieDetails

        if (siteId != other.siteId) return false
        if (pageId != other.pageId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = siteId
        result = 31 * result + pageId.hashCode()
        return result
    }

}
