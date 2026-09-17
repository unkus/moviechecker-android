package ru.moviechecker.database.season

import androidx.room3.Embedded
import androidx.room3.Relation
import ru.moviechecker.database.episode.EpisodeEntity

data class SeasonWithEpisodes(
    @Embedded val season: SeasonEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["season_id"]
    )
    val episodes: List<EpisodeEntity>
)
