package ru.moviechecker.database.season

import androidx.room.Embedded
import androidx.room.Relation
import ru.moviechecker.database.episode.EpisodeEntity

data class SeasonWithEpisodes(
    @Embedded val season: SeasonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "season_id"
    )
    val episodes: List<EpisodeEntity>
)
