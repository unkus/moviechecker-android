package ru.moviechecker.database.movies

import androidx.room.ColumnInfo

data class MovieDetails(
    val id: Int,
    @ColumnInfo(name = "site_id")
    val siteId: Int,
    val address: String,
    @ColumnInfo(name = "page_id")
    val pageId: String,
    var title: String,
    var link: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var poster: ByteArray,
    @ColumnInfo(name = "favorites_mark")
    var favoritesMark: Boolean
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
