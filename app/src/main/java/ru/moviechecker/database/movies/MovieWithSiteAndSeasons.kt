package ru.moviechecker.database.movies

import androidx.room.Embedded
import androidx.room.Relation
import ru.moviechecker.database.seasons.SeasonEntity
import ru.moviechecker.database.seasons.SeasonWithEpisodes
import ru.moviechecker.database.sites.SiteEntity

data class MovieWithSiteAndSeasons(
    @Embedded val movie: MovieEntity,
    @Relation(
        parentColumn = "site_id",
        entityColumn = "id"
    )
    val site: SiteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "movie_id",
        entity = SeasonEntity::class
    )
    val seasons: List<SeasonWithEpisodes>
)
