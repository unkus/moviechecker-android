package com.example.database.episode

import android.net.Uri
import androidx.room.DatabaseView
import com.example.database.Linkable
import com.example.database.Numerated
import com.example.database.api.State
import com.example.database.Titled
import java.time.LocalDateTime

@DatabaseView("SELECT site.link as siteAddress, " +
        "movie.page_id as moviePageId, " +
        "movie.title as movieTitle, " +
        "movie.poster_link as posterLink, " +
        "season.number as seasonNumber, " +
        "e.number, " +
        "e.title, " +
        "(site.link || TRIM(season.link, \".html\") || e.link) as link, " +
        "e.state, " +
        "e.date, " +
//        "CAST((SELECT id FROM favorite WHERE movie_id = movie.id) AS BOOLEAN) as isInFavorite " +
        "EXISTS(SELECT id FROM favorite WHERE movie_id = movie.id) as isInFavorite " +
        "FROM episode e " +
        "INNER JOIN site, movie, season " +
        "ON site.id = movie.site_id " +
        "AND movie.id = season.movie_id " +
        "AND season.id = e.season_id")
data class EpisodeDetail(
    val siteAddress: Uri,
    val moviePageId: String,
    val movieTitle: String,
    val posterLink: Uri?,
    val seasonNumber: Int,
    override val number: Int,
    override val title: String,
    override val link: Uri,
    val state: State,
    val date: LocalDateTime,
    val isInFavorite: Boolean
): Numerated, Titled, Linkable
