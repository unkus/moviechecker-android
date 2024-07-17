package ru.moviechecker.database.sites

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import ru.moviechecker.database.movies.MovieEntity

@Entity
data class SiteWithMovies(
    @Embedded val site: SiteEntity,
    @Relation(
        parentColumn = "movie_id",
        entityColumn = "id"
    )
    val movies: List<MovieEntity>
)
