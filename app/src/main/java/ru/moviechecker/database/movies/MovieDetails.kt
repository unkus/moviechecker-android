package ru.moviechecker.database.movies

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.net.URI

data class MovieDetails(
    val id: Int = 0,
    @ColumnInfo(name = "page_id")
    val pageId: String,
    var title: String,
    var link: String? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var poster: ByteArray? = null,
    @ColumnInfo(name = "favorites_mark")
    var favoritesMark: Boolean = false,
    @ColumnInfo(name = "site_address")
    val siteAddress: URI,
    @ColumnInfo(name = "season_count")
    val seasonCount: Int
) {

}
