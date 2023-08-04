package moviechecker.data.favorite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import moviechecker.data.movie.MovieEntity

@Entity(
    tableName = "favorite",
    foreignKeys = [ForeignKey(
        entity = MovieEntity::class,
        parentColumns = ["id"],
        childColumns = ["movie_id"]
    )],
    indices = [Index(value = ["movie_id"], unique = true)]
)
data class FavoriteEntity(
    @ColumnInfo(name = "movie_id") val movieId: Int
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    @ColumnInfo(name = "last_viewed") var lastViewed: Int? = null

    override fun toString(): String = "Favorite(movieId=$movieId)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FavoriteEntity

        if (movieId != other.movieId) return false

        return true
    }

    override fun hashCode(): Int {
        return movieId
    }
}