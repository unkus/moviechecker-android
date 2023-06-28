package com.example.moviechecker.model.favorite

import android.net.Uri
import androidx.room.DatabaseView
import com.example.moviechecker.model.Linkable
import com.example.moviechecker.model.Titled

@DatabaseView("SELECT s.link as siteAddress, " +
        "m.page_id as moviePageId, " +
        "m.title, " +
        "(s.link || m.link) as link " +
        "FROM favorite f " +
        "INNER JOIN site s, movie m " +
        "ON s.id = m.site_id " +
        "AND m.id = f.movie_id")
data class FavoriteDetail(
    val siteAddress: Uri,
    val moviePageId: String,
    override val title: String,
    override val link: Uri
): Titled, Linkable
