package com.moviechecker.database.movies

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.moviechecker.database.seasons.SeasonEntity

@Entity
data class MovieWithSeasons(
    @Embedded val movie: MovieEntity,
    @Relation(
        parentColumn = "season_id",
        entityColumn = "id"
    )
    val seasons: List<SeasonEntity>
)
