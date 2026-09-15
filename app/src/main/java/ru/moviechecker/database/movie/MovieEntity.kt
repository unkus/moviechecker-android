package ru.moviechecker.database.movie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "movies", indices = [Index(value = ["site_id", "page_id"], unique = true)])
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "site_id")
    val siteId: Int = 0,
    @ColumnInfo(name = "page_id")
    val pageId: String,
    var title: String,
    var link: String? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var poster: ByteArray? = null,
    @ColumnInfo(name = "favorites_mark")
    var favoritesMark: Boolean = false,
    var kinopoiskId: String? = null
) {
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
