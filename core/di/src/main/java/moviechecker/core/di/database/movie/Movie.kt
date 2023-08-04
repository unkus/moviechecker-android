package moviechecker.core.di.database.movie

import moviechecker.core.di.Linkable
import java.net.URI

interface Movie : Linkable {
    val siteId: Int
    val pageId: String
    val title: String
    val posterLink: URI?
}