package com.example.moviechecker.model.favorite

import android.net.Uri
import androidx.room.DatabaseView

@DatabaseView("SELECT s.link as siteAddress, " +
        "m.page_id as moviePageId, " +
        "m.title as movieTitle, " +
        "m.link as movieLink " +
        "FROM favorite f " +
        "INNER JOIN site s, movie m " +
        "ON s.id = m.site_id " +
        "AND m.id = f.movie_id")
data class FavoriteDetail(
    val siteAddress: Uri,
    val moviePageId: String,
    val movieTitle: String,
    val movieLink: Uri
)
