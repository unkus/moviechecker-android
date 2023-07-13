package com.example.database.movie

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.database.Linkable
import com.example.database.Titled
import com.example.database.site.Site

@Entity(
    tableName = "movie",
    foreignKeys = [ForeignKey(
        entity = Site::class,
        parentColumns = ["id"],
        childColumns = ["site_id"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["site_id", "page_id"], unique = true)]
)
data class Movie(
    @ColumnInfo(name = "site_id") val siteId: Int,
    @ColumnInfo(name = "page_id") val pageId: String,
    override var title: String,
    override var link: Uri,
    @ColumnInfo(name = "poster_link") var posterLink: Uri? = null
): Titled, Linkable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    override fun toString(): String {
        return "Movie(siteId=$siteId, pageId='$pageId')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Movie

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