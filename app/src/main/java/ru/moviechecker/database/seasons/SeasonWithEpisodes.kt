package ru.moviechecker.database.seasons

import androidx.room.Embedded
import androidx.room.Relation
import ru.moviechecker.database.episodes.EpisodeEntity

data class SeasonWithEpisodes(
    @Embedded val season: SeasonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "season_id"
    )
    val episodes: List<EpisodeEntity>
)
