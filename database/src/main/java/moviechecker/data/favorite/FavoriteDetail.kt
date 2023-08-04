package moviechecker.data.favorite

import androidx.room.DatabaseView
import moviechecker.core.di.database.favorite.Favorite
import java.net.URI

@DatabaseView("SELECT s.link as siteAddress, " +
        "m.page_id as moviePageId, " +
        "m.title, " +
        "(s.link || m.link) as link " +
        "FROM favorite f " +
        "INNER JOIN site s, movie m " +
        "ON s.id = m.site_id " +
        "AND m.id = f.movie_id")
data class FavoriteDetail(
    override val siteAddress: URI,
    override val moviePageId: String,
    override val title: String,
    override val link: URI
): Favorite
