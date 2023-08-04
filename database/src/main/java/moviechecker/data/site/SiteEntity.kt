package moviechecker.data.site

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import moviechecker.core.di.database.site.Site
import java.net.URI

@Entity(tableName = "site", indices = [Index(value = ["link"], unique = true)])
data class SiteEntity(
    override val link: URI
): Site {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}