package com.example.moviechecker.model.episode

import android.net.Uri
import androidx.room.DatabaseView
import com.example.moviechecker.model.State
import java.time.LocalDateTime

@DatabaseView("SELECT site.link as siteAddress, " +
        "movie.page_id as moviePageId, " +
        "movie.title as movieTitle, " +
        "season.number as seasonNumber, " +
        "e.number, " +
        "e.title, " +
        "e.link, " +
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
    val seasonNumber: Long,
    val number: Long,
    val title: String,
    val link: Uri,
    val state: State,
    val date: LocalDateTime,
    val isInFavorite: Boolean
)
