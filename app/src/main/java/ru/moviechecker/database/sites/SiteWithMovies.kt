package ru.moviechecker.database.sites

import androidx.room.Embedded
import androidx.room.Relation
import ru.moviechecker.database.movies.MovieEntity

data class SiteWithMovies(
    @Embedded val site: SiteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "site_id"
    )
    val movies: List<MovieEntity>
)
