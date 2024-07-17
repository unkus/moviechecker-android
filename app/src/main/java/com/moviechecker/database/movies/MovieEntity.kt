package com.moviechecker.database.movies

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.net.URI
import java.sql.Blob

@Entity(tableName = "movies", indices = [Index(value = ["site_id", "page_id"], unique = true)])
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    @ColumnInfo(name = "site_id") override val siteId: Int = 0,
    @ColumnInfo(name = "page_id") override val pageId: String,
    override var title: String,
    override var link: URI? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) override var poster: ByteArray? = null,
    @ColumnInfo(name = "favorites_mark") override var favoritesMark: Boolean = false
) : IMovie {
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
