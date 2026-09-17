package ru.moviechecker.database.episode

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "episodes", indices = [Index(value = ["season_id", "number"], unique = true), Index(value = ["state"])])
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "season_id")
    val seasonId: Int = 0,
    val number: Int,
    var title: String? = null,
    var link: String,
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
