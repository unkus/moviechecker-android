package moviechecker.core.di.database.favorite

import moviechecker.core.di.Linkable
import java.net.URI

interface Favorite : Linkable {
    val siteAddress: URI
    val moviePageId: String
    val title: String
}