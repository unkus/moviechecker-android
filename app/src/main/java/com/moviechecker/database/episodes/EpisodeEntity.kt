package com.moviechecker.database.episodes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.net.URI
import java.time.LocalDateTime

@Entity(tableName = "episodes", indices = [Index(value = ["season_id", "number"], unique = true)])
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "season_id") val seasonId: Int = 0,
    val number: Int,
    var title: String?,
    var link: URI,
    var state: EpisodeState,
    var date: LocalDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EpisodeEntity

        if (number != other.number) return false
        if (seasonId != other.seasonId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = number
        result = 31 * result + seasonId
        return result
    }

}
