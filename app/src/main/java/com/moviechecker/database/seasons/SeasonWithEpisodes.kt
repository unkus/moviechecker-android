package com.moviechecker.database.seasons

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.moviechecker.database.episodes.EpisodeEntity

@Entity
data class SeasonWithEpisodes(
    @Embedded val seasonEntity: SeasonEntity,
    @Relation(
        parentColumn = "episode_id",
        entityColumn = "id"
    )
    val episodes: List<EpisodeEntity>
)
