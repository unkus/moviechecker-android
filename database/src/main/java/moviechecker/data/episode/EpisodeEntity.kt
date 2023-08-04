package moviechecker.data.episode

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import moviechecker.data.season.SeasonEntity
import moviechecker.core.di.State
import java.net.URI
import java.time.LocalDateTime

@Entity(
    tableName = "episode",
    foreignKeys = [ForeignKey(
        entity = SeasonEntity::class,
        parentColumns = ["id"],
        childColumns = ["season_id"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["season_id", "number"], unique = true)]
)
data class EpisodeEntity(
    @ColumnInfo(name = "season_id") val seasonId: Int,
    val number: Int,
    var title: String,
    var link: URI,
    var state: State = State.EXPECTED,
    var date: LocalDateTime
    ) {

    @PrimaryKey(autoGenerate = true) var id: Int = 0

    override fun toString(): String = "Episode(seasonId=$seasonId, number=$number)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EpisodeEntity

        if (seasonId != other.seasonId) return false
        if (number != other.number) return false

        return true
    }

    override fun hashCode(): Int {
        var result = seasonId
        result = 31 * result + number
        return result
    }

}