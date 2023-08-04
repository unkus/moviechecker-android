package moviechecker.data.season

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import moviechecker.core.di.database.season.Season
import moviechecker.data.movie.MovieEntity
import java.net.URI

@Entity(
    tableName = "season",
    foreignKeys = [ForeignKey(
        entity = MovieEntity::class,
        parentColumns = ["id"],
        childColumns = ["movie_id"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["movie_id", "number"], unique = true)]
)
data class SeasonEntity(
    @ColumnInfo(name = "movie_id") override val movieId: Int,
    override val number: Int,
    override val link: URI
): Season {
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    val title: String
        get() = "$movieId $number"

    override fun toString(): String = "Season(movieId=$movieId, number=$number)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeasonEntity

        if (movieId != other.movieId) return false
        if (number != other.number) return false

        return true
    }

    override fun hashCode(): Int {
        var result = movieId
        result = 31 * result + number
        return result
    }

}