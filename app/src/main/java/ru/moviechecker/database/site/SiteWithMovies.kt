package ru.moviechecker.database.site

import androidx.room3.Embedded
import androidx.room3.Relation
import ru.moviechecker.database.movie.MovieEntity

data class SiteWithMovies(
    @Embedded val site: SiteEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["site_id"]
    )
    val movies: List<MovieEntity>
)
