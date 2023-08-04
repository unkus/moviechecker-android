package moviechecker.core.di.database.episode

import moviechecker.core.di.Linkable
import moviechecker.core.di.State
import java.net.URI
import java.time.LocalDateTime

interface Episode : Linkable {
    val siteAddress: URI
    val moviePageId: String
    val movieTitle: String
    val posterLink: URI?

    val seasonNumber: Int
    val number: Int
    val title: String
    val state: State
    val date: LocalDateTime
    val isInFavorite: Boolean
}