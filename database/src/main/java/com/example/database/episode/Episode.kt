package com.example.database.episode

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.database.Linkable
import com.example.database.Numerated
import com.example.database.season.Season
import com.example.database.api.State
import com.example.database.Titled
import java.time.LocalDateTime

@Entity(
    tableName = "episode",
    foreignKeys = [ForeignKey(
        entity = Season::class,
        parentColumns = ["id"],
        childColumns = ["season_id"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["season_id", "number"], unique = true)]
)
data class Episode(
    @ColumnInfo(name = "season_id") val seasonId: Int,
    override val number: Int,
    override var title: String,
    override var link: Uri,
    var state: State = State.EXPECTED,
    var date: LocalDateTime
    ): Numerated, Titled, Linkable {

    @PrimaryKey(autoGenerate = true) var id: Int = 0

    override fun toString(): String {
        return "Episode(seasonId=$seasonId, number=$number)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Episode

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