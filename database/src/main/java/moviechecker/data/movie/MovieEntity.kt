package moviechecker.data.movie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import moviechecker.core.di.database.movie.Movie
import moviechecker.data.site.SiteEntity
import java.net.URI

@Entity(
    tableName = "movie",
    foreignKeys = [ForeignKey(
        entity = SiteEntity::class,
        parentColumns = ["id"],
        childColumns = ["site_id"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["site_id", "page_id"], unique = true)]
)
data class MovieEntity(
    @ColumnInfo(name = "site_id") override val siteId: Int,
    @ColumnInfo(name = "page_id") override val pageId: String,
    override var title: String,
    override var link: URI,
    @ColumnInfo(name = "poster_link") override var posterLink: URI? = null
): Movie {
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    override fun toString(): String = "Movie(siteId=$siteId, pageId='$pageId')"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieEntity

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